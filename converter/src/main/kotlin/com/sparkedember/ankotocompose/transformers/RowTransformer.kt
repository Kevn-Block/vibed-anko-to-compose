package com.sparkedember.ankotocompose.transformers

import com.sparkedember.ankotocompose.core.*
import com.sparkedember.ankotocompose.config.ComposeImports

/**
 * Transformer for horizontal layouts (linearLayout with horizontal orientation)
 */
class RowTransformer : LayoutTransformer {
    
    override fun canTransform(node: LayoutNode): Boolean {
        return node.type == "horizontalLinearLayout" ||
               (node.type == "linearLayout" && node.properties["orientation"] == "LinearLayout.HORIZONTAL")
    }
    
    override fun transform(node: LayoutNode, context: TransformationContext): TransformationResult {
        val requiredImports = mutableSetOf<String>()
        requiredImports.add(ComposeImports.ROW)
        requiredImports.add(ComposeImports.MODIFIER)
        
        // Transform children
        val childrenCode = node.children.map { child ->
            val transformer = context.availableTransformers.firstOrNull { it.canTransform(child) }
                ?: throw ConversionException("No transformer found for child type: ${child.type}")
            
            val childResult = transformer.transform(child, context.withIncreasedDepth())
            requiredImports.addAll(childResult.requiredImports)
            childResult.code
        }
        
        // Build the Row code
        val rowContent = if (childrenCode.isNotEmpty()) {
            childrenCode.joinToString("\n") { "    $it" }
        } else {
            "    // Empty layout"
        }
        
        val code = "Row {\n$rowContent\n}"
        
        return TransformationResult(
            code = code,
            requiredImports = requiredImports
        )
    }
    
    override val priority: Int = 10
}