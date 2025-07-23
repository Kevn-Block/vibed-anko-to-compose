package com.sparkedember.ankotocompose.transformers

import com.sparkedember.ankotocompose.core.*
import com.sparkedember.ankotocompose.config.ComposeImports

/**
 * Transformer for frameLayout to Box
 */
class BoxTransformer : LayoutTransformer {
    
    override fun canTransform(node: LayoutNode): Boolean {
        return node.type == "frameLayout"
    }
    
    override fun transform(node: LayoutNode, context: TransformationContext): TransformationResult {
        val requiredImports = mutableSetOf<String>()
        requiredImports.add(ComposeImports.BOX)
        requiredImports.add(ComposeImports.MODIFIER)
        
        // Transform children
        val childrenCode = node.children.map { child ->
            val transformer = context.availableTransformers.firstOrNull { it.canTransform(child) }
                ?: throw ConversionException("No transformer found for child type: ${child.type}")
            
            val childResult = transformer.transform(child, context.withIncreasedDepth())
            requiredImports.addAll(childResult.requiredImports)
            childResult.code
        }
        
        // Build the Box code
        val boxContent = if (childrenCode.isNotEmpty()) {
            childrenCode.joinToString("\n") { "    $it" }
        } else {
            "    // Empty layout"
        }
        
        val code = "Box {\n$boxContent\n}"
        
        return TransformationResult(
            code = code,
            requiredImports = requiredImports
        )
    }
    
    override val priority: Int = 10
}