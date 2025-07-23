package com.sparkedember.ankotocompose.transformers

import com.sparkedember.ankotocompose.ModifierParser
import com.sparkedember.ankotocompose.Transformer
import org.jetbrains.kotlin.psi.*

/**
 * Transformer for checkBox to Row with Checkbox and Text
 */
class CheckBoxTransformer : Transformer {
    override fun transform(expression: KtCallExpression, factory: KtPsiFactory): KtElement {
        // Get the lambda expression from the checkBox call
        val lambdaExpression = expression.lambdaArguments.firstOrNull()?.getLambdaExpression()
        val lambdaBody = lambdaExpression?.bodyExpression

        // Default values
        var textValue = "\"\""
        var isCheckedValue = "false"
        val processedStatements = mutableListOf<KtExpression>()

        // Extract text and isChecked properties if lambda body exists
        if (lambdaBody != null) {
            lambdaBody.statements.forEach { statement ->
                if (statement is KtBinaryExpression && statement.operationToken.toString() == "=") {
                    val left = statement.left?.text ?: return@forEach
                    val right = statement.right ?: return@forEach

                    when (left) {
                        "text" -> {
                            textValue = right.text
                            processedStatements.add(statement)
                        }
                        "isChecked" -> {
                            isCheckedValue = right.text
                            processedStatements.add(statement)
                        }
                    }
                }
            }

            // Remove processed statements
            processedStatements.forEach { it.delete() }
        }

        // Check for lparams
        val modifierString = processLparams(expression)

        // Create the Row with Checkbox and Text
        return if (modifierString != null && modifierString != "Modifier") {
            factory.createExpression("""
                Row(
                    modifier = $modifierString,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = $isCheckedValue,
                        onCheckedChange = { /* TODO: Handle state change */ }
                    )
                    Text(
                        text = $textValue,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            """.trimIndent())
        } else {
            factory.createExpression("""
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = $isCheckedValue,
                        onCheckedChange = { /* TODO: Handle state change */ }
                    )
                    Text(
                        text = $textValue,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            """.trimIndent())
        }
    }
}