package com.sparkedember.ankotocompose

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.psi.PsiRecursiveElementVisitor
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.*

/**
 * Main function that demonstrates the conversion from Anko to Compose
 */
fun main() {
    val ankoCode = """
        verticalLayout {
            textView("Hello, Anko!")
            
            button("Click Here") {
                onClick {
                    println("Button was clicked!")
                }
            }
        }
    """.trimIndent()
    
    println("Original Anko code:")
    println(ankoCode)
    println("\nConverted to Compose:")
    
    val composeCode = convertAnkoToCompose(ankoCode)
    println(composeCode)
}

/**
 * Converts Anko DSL code to Jetpack Compose code using AST transformation
 */
fun convertAnkoToCompose(ankoCode: String): String {
    // Set up the Kotlin compiler environment
    val disposable = Disposer.newDisposable()
    val configuration = CompilerConfiguration()
    configuration.put(
        CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
        PrintingMessageCollector(System.err, MessageRenderer.PLAIN_FULL_PATHS, false)
    )
    
    val environment = KotlinCoreEnvironment.createForProduction(
        disposable,
        configuration,
        EnvironmentConfigFiles.JVM_CONFIG_FILES
    )
    
    // Create a virtual file with the Anko code
    val virtualFile = LightVirtualFile("temp.kt", KotlinFileType.INSTANCE, ankoCode)
    val psiFile = PsiManager.getInstance(environment.project).findFile(virtualFile) as KtFile
    
    // Create a visitor to transform the AST
    val visitor = AnkoToComposeVisitor()
    psiFile.accept(visitor)
    
    // Generate the Compose code with imports
    val imports = """
        import androidx.compose.foundation.layout.Column
        import androidx.compose.material.Button
        import androidx.compose.material.Text
    """.trimIndent()
    
    return "$imports\n\n${visitor.getTransformedCode()}"
}

/**
 * Visitor class that traverses the Kotlin AST and transforms Anko elements to Compose elements
 */
class AnkoToComposeVisitor : PsiRecursiveElementVisitor() {
    private val transformedCode = StringBuilder()
    private var indentLevel = 0
    
    fun getTransformedCode(): String = transformedCode.toString()
    
    override fun visitElement(element: PsiElement) {
        when (element) {
            is KtCallExpression -> handleCallExpression(element)
            else -> super.visitElement(element)
        }
    }
    
    private fun handleCallExpression(callExpression: KtCallExpression) {
        val calleeText = callExpression.calleeExpression?.text
        
        when (calleeText) {
            "verticalLayout" -> {
                // Transform verticalLayout to Column
                transformedCode.append("Column {\n")
                indentLevel++
                
                // Process children
                val lambdaArg = callExpression.lambdaArguments.firstOrNull()
                lambdaArg?.getLambdaExpression()?.bodyExpression?.statements?.forEach { statement ->
                    statement.accept(this)
                }
                
                indentLevel--
                transformedCode.append("}")
            }
            "textView" -> {
                // Transform textView to Text
                val args = callExpression.valueArguments
                if (args.isNotEmpty()) {
                    val textArg = args.first().getArgumentExpression()?.text ?: ""
                    transformedCode.append("Text($textArg)\n")
                }
            }
            "button" -> {
                // Transform button to Button
                val args = callExpression.valueArguments
                val lambdaArg = callExpression.lambdaArguments.firstOrNull()
                
                if (args.isNotEmpty() && lambdaArg != null) {
                    val buttonText = args.first().getArgumentExpression()?.text ?: ""
                    
                    // Extract onClick lambda
                    var onClickLambda = ""
                    lambdaArg.getLambdaExpression()?.bodyExpression?.statements?.forEach { statement ->
                        if (statement is KtCallExpression && statement.calleeExpression?.text == "onClick") {
                            val onClickBody = statement.lambdaArguments.firstOrNull()
                                ?.getLambdaExpression()?.bodyExpression?.text ?: ""
                            onClickLambda = onClickBody
                        }
                    }
                    
                    // Create Button with onClick parameter and Text content
                    transformedCode.append("Button(onClick = {\n")
                    transformedCode.append("    $onClickLambda\n")
                    transformedCode.append("}) {\n")
                    transformedCode.append("    Text($buttonText)\n")
                    transformedCode.append("}\n")
                }
            }
            else -> super.visitElement(callExpression)
        }
    }
}