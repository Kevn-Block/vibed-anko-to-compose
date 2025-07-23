package com.sparkedember.ankotocompose.transformers

import com.sparkedember.ankotocompose.ModifierParser
import com.sparkedember.ankotocompose.Transformer
import org.jetbrains.kotlin.psi.*

/**
 * Transformer for scrollView to Column with verticalScroll modifier
 */
class ScrollViewTransformer : Transformer {
    override fun transform(expression: KtCallExpression, factory: KtPsiFactory): KtElement {
        // Get the lambda expression from the scrollView call
        val lambdaExpression = expression.lambdaArguments.firstOrNull()?.getLambdaExpression()
        val lambdaBody = lambdaExpression?.bodyExpression

        if (lambdaBody == null) {
            // If there's no lambda body, create a basic Column with verticalScroll
            val lambdaArg = lambdaExpression?.text ?: "{}"
            val modifierString = processLparams(expression)

            return if (modifierString != null && modifierString != "Modifier") {
                factory.createExpression("Column(\nmodifier = $modifierString.verticalScroll(rememberScrollState())\n) $lambdaArg")
            } else {
                factory.createExpression("Column(\nmodifier = Modifier.verticalScroll(rememberScrollState())\n) $lambdaArg")
            }
        }

        // Parse property assignments from the lambda body
        val propertyResult = ModifierParser.parsePropertyAssignments(lambdaBody)

        // Check for lparams
        val lparamsModifier = processLparams(expression)

        // Combine modifiers from both sources and add verticalScroll
        val combinedModifier = if (lparamsModifier != null && lparamsModifier != "Modifier") {
            if (propertyResult.modifierString != "Modifier") {
                // Both sources have modifiers, combine them
                val lparamsChain = lparamsModifier.substring("Modifier".length) // Remove the "Modifier" prefix
                "${propertyResult.modifierString}$lparamsChain.verticalScroll(rememberScrollState())"
            } else {
                // Only lparams has modifiers
                "$lparamsModifier.verticalScroll(rememberScrollState())"
            }
        } else {
            // Only property assignments have modifiers, or neither has modifiers
            if (propertyResult.modifierString != "Modifier") {
                "${propertyResult.modifierString}.verticalScroll(rememberScrollState())"
            } else {
                "Modifier.verticalScroll(rememberScrollState())"
            }
        }

        // Remove processed property assignments from the lambda body
        propertyResult.processedStatements.forEach { it.delete() }

        // Create the new expression with the combined modifier
        return factory.createExpression("Column(\nmodifier = $combinedModifier\n) ${lambdaExpression.text}")
    }
}