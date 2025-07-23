package com.sparkedember.ankotocompose.transformers

import com.sparkedember.ankotocompose.core.*
import com.sparkedember.ankotocompose.config.ComposeImports

/**
 * Transformer for TextView to Text
 */
class TextViewTransformer : LayoutTransformer {
    
    override fun canTransform(node: LayoutNode): Boolean {
        return node.type == "textView"
    }
    
    override fun transform(node: LayoutNode, context: TransformationContext): TransformationResult {
        val requiredImports = mutableSetOf<String>()
        requiredImports.add(ComposeImports.TEXT)
        
        // Extract text from properties (could be in properties or from the raw lambda body)
        val text = node.properties["text"]?.toString()?.removeSurrounding("\"") 
            ?: extractTextFromLambdaBody(node.lambdaBody)
            ?: "Text"
        
        val modifierParts = mutableListOf<String>()
        
        // Handle properties
        node.properties.forEach { (key, value) ->
            when (key) {
                "textSize" -> {
                    // Handle textSize conversion
                    val sizeValue = value.toString().replace("f", "")
                    modifierParts.add("fontSize = ${sizeValue}.sp")
                    requiredImports.add(ComposeImports.SP)
                }
            }
        }
        
        // Build the Text composable
        val parameters = mutableListOf("\"$text\"")
        
        if (modifierParts.isNotEmpty()) {
            parameters.add("modifier = Modifier")
            // Add other parameters like fontSize
            node.properties["textSize"]?.let {
                val sizeValue = it.toString().replace("f", "")
                parameters.add("fontSize = ${sizeValue}.sp")
            }
        }
        
        val code = "Text(${parameters.joinToString(", ")})"
        
        return TransformationResult(
            code = code,
            requiredImports = requiredImports
        )
    }
    
    private fun extractTextFromLambdaBody(lambdaBody: String?): String? {
        if (lambdaBody == null) return null
        
        // Look for textView("...") pattern in lambda body
        val textMatch = """textView\("([^"]*)"\)""".toRegex().find(lambdaBody)
        return textMatch?.groupValues?.get(1)
    }
    
    override val priority: Int = 5
}