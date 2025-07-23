package com.sparkedember.ankotocompose.parsing

import com.sparkedember.ankotocompose.core.*
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.*

/**
 * AST-based parser using Kotlin compiler PSI
 */
class AstParser : Parser {
    
    override fun parse(ankoCode: String): ParseResult {
        val disposable = Disposer.newDisposable()
        try {
            val environment = createKotlinEnvironment(disposable)
            val psiFile = createPsiFile(ankoCode, environment)
            
            val customFunctions = extractCustomFunctions(psiFile)
            val mainLayoutCode = extractMainLayoutCode(ankoCode)
            val layoutTree = parseLayoutTree(mainLayoutCode, environment)
            
            return ParseResult(
                layoutTree = layoutTree,
                customFunctions = customFunctions,
                psiFile = psiFile
            )
        } catch (e: Exception) {
            throw ParseException("Failed to parse Anko code with AST", e)
        } finally {
            disposable.dispose()
        }
    }
    
    private fun createKotlinEnvironment(disposable: org.jetbrains.kotlin.com.intellij.openapi.Disposable): KotlinCoreEnvironment {
        val configuration = CompilerConfiguration()
        configuration.put(
            CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY,
            PrintingMessageCollector(System.err, MessageRenderer.PLAIN_FULL_PATHS, false)
        )
        
        return KotlinCoreEnvironment.createForProduction(
            disposable,
            configuration,
            EnvironmentConfigFiles.JVM_CONFIG_FILES
        )
    }
    
    private fun createPsiFile(code: String, environment: KotlinCoreEnvironment): KtFile {
        val virtualFile = LightVirtualFile("temp.kt", KotlinFileType.INSTANCE, code)
        return PsiManager.getInstance(environment.project).findFile(virtualFile) as KtFile
    }
    
    private fun extractCustomFunctions(psiFile: KtFile): Map<String, String> {
        val customFunctions = mutableMapOf<String, String>()
        
        psiFile.accept(object : KtTreeVisitorVoid() {
            override fun visitNamedFunction(function: KtNamedFunction) {
                super.visitNamedFunction(function)
                
                if (isCustomAnkoFunction(function)) {
                    val functionName = function.name
                    if (functionName != null) {
                        customFunctions[functionName] = convertCustomFunction(function)
                    }
                }
            }
        })
        
        return customFunctions
    }
    
    private fun isCustomAnkoFunction(function: KtNamedFunction): Boolean {
        val receiverType = function.receiverTypeReference?.text
        if (receiverType != null) {
            return receiverType.contains("_LinearLayout") || 
                   receiverType.contains("_RelativeLayout") ||
                   receiverType.contains("AnkoViewDslMarker")
        }
        
        val annotations = function.annotationEntries
        return annotations.any { it.text.contains("AnkoViewDslMarker") }
    }
    
    private fun convertCustomFunction(function: KtNamedFunction): String {
        val functionName = function.name ?: "UnknownFunction"
        val capitalizedName = functionName.replaceFirstChar { it.uppercase() }
        
        val parameters = function.valueParameters.joinToString(", ") { 
            "${it.name}: ${it.typeReference?.text ?: "Any"}" 
        }
        
        val visibilityModifier = function.modifierList?.children
            ?.firstOrNull { it.text in setOf("private", "internal", "protected", "public") }
            ?.text ?: "private"
        
        // For now, return a simple stub - would need more complex transformation
        return """
            @Composable
            $visibilityModifier fun $capitalizedName($parameters) {
                // TODO: Convert function body
            }
        """.trimIndent()
    }
    
    private fun extractMainLayoutCode(ankoCode: String): String {
        val layoutKeys = listOf("frameLayout", "verticalLayout", "linearLayout", "coordinatorLayout", "scrollView", "checkBox", "switch")
        val mainLayoutStart = layoutKeys
            .map { ankoCode.indexOf(it) }
            .filter { it != -1 }
            .minOrNull() ?: return ankoCode
            
        return ankoCode.substring(mainLayoutStart)
    }
    
    private fun parseLayoutTree(layoutCode: String, environment: KotlinCoreEnvironment): LayoutNode {
        // Wrap in a function to parse correctly
        val wrappedCode = """
            fun tempFunction() {
                $layoutCode
            }
        """.trimIndent()
        
        val psiFile = createPsiFile(wrappedCode, environment)
        val functionBody = findFunctionBody(psiFile) 
            ?: throw ParseException("Could not find function body")
        
        // Extract the main call expression
        val mainCall = functionBody.statements.firstOrNull() as? KtCallExpression
            ?: throw ParseException("No main layout call found")
        
        return convertCallExpressionToLayoutNode(mainCall)
    }
    
    private fun findFunctionBody(psiFile: KtFile): KtBlockExpression? {
        var functionBody: KtBlockExpression? = null
        
        psiFile.accept(object : KtTreeVisitorVoid() {
            override fun visitNamedFunction(function: KtNamedFunction) {
                super.visitNamedFunction(function)
                if (function.name == "tempFunction") {
                    functionBody = function.bodyBlockExpression
                }
            }
        })
        
        return functionBody
    }
    
    private fun convertCallExpressionToLayoutNode(call: KtCallExpression): LayoutNode {
        val type = call.calleeExpression?.text ?: "unknown"
        val properties = mutableMapOf<String, Any>()
        val children = mutableListOf<LayoutNode>()
        
        // Extract value arguments (e.g., textView("text"))
        call.valueArguments.forEach { argument ->
            val argumentExpression = argument.getArgumentExpression()
            if (argumentExpression != null) {
                // For textView("text"), this would be the text argument
                if (type == "textView" && argumentExpression.text.startsWith("\"")) {
                    properties["text"] = argumentExpression.text.removeSurrounding("\"")
                }
            }
        }
        
        // Extract lambda body and analyze it
        val lambdaExpression = call.lambdaArguments.firstOrNull()?.getLambdaExpression()
        val lambdaBody = lambdaExpression?.bodyExpression
        
        if (lambdaBody != null) {
            lambdaBody.statements.forEach { statement ->
                when (statement) {
                    is KtBinaryExpression -> {
                        // Property assignment
                        if (statement.operationToken.toString() == "=") {
                            val propertyName = statement.left?.text
                            val propertyValue = statement.right?.text
                            if (propertyName != null && propertyValue != null) {
                                properties[propertyName] = propertyValue
                            }
                        }
                    }
                    is KtCallExpression -> {
                        // Child element
                        children.add(convertCallExpressionToLayoutNode(statement))
                    }
                }
            }
        }
        
        return LayoutNode(
            type = type,
            properties = properties,
            children = children,
            lambdaBody = lambdaBody?.text
        )
    }
}