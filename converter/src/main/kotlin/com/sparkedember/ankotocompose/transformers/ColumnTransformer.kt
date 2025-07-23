package com.sparkedember.ankotocompose.transformers

import com.sparkedember.ankotocompose.core.*
import com.sparkedember.ankotocompose.config.ComposeImports

/**
 * Transformer for vertical layouts (verticalLayout, linearLayout without horizontal orientation)
 */
class ColumnTransformer : LayoutTransformer {
    
    override fun canTransform(node: LayoutNode): Boolean {
        return node.type in setOf("verticalLayout", "verticalLinearLayout", "linearLayout") &&
               node.properties["orientation"] != "LinearLayout.HORIZONTAL"
    }
    
    override fun transform(node: LayoutNode, context: TransformationContext): TransformationResult {
        val modifierParts = mutableListOf<String>()
        val requiredImports = mutableSetOf<String>()
        
        // Add required imports
        requiredImports.add(ComposeImports.COLUMN)
        requiredImports.add(ComposeImports.MODIFIER)
        
        // Handle properties
        node.properties.forEach { (key, value) ->
            when (key) {
                "backgroundColor" -> {
                    modifierParts.add("background(${convertColor(value.toString())})")
                    requiredImports.add(ComposeImports.BACKGROUND)
                    requiredImports.add(ComposeImports.COLOR)
                }
                "padding" -> {
                    modifierParts.add("padding(${convertDimension(value.toString())})")
                    requiredImports.add(ComposeImports.PADDING)
                    requiredImports.add(ComposeImports.DP)
                }
            }
        }
        
        // Build modifier string
        val modifierString = if (modifierParts.isNotEmpty()) {
            "modifier = Modifier${modifierParts.joinToString("") { ".$it" }}"
        } else {
            ""
        }
        
        // Transform children
        val childrenCode = node.children.map { child ->
            val transformer = context.availableTransformers.firstOrNull { it.canTransform(child) }
                ?: throw ConversionException("No transformer found for child type: ${child.type}")
            
            val childResult = transformer.transform(child, context.withIncreasedDepth())
            requiredImports.addAll(childResult.requiredImports)
            childResult.code
        }
        
        // Build the Column code
        val columnContent = if (childrenCode.isNotEmpty()) {
            childrenCode.joinToString("\n") { "    $it" }
        } else {
            "    // Empty layout"
        }
        
        val code = if (modifierString.isNotEmpty()) {
            "Column(\n    $modifierString\n) {\n$columnContent\n}"
        } else {
            "Column {\n$columnContent\n}"
        }
        
        return TransformationResult(
            code = code,
            requiredImports = requiredImports
        )
    }
    
    private fun convertColor(colorString: String): String {
        return when {
            colorString.startsWith("Color.parseColor") -> {
                val hexMatch = """Color\.parseColor\("([^"]*)"\)""".toRegex().find(colorString)
                if (hexMatch != null) {
                    val hex = hexMatch.groupValues[1]
                    "Color(0xFF${hex.removePrefix("#")})"
                } else {
                    "Color.Gray"
                }
            }
            colorString.startsWith("Color.") -> {
                val colorName = colorString.substringAfter("Color.")
                "Color.$colorName"
            }
            else -> colorString
        }
    }
    
    private fun convertDimension(dimension: String): String {
        return when {
            dimension.contains("dip(") -> {
                val dipMatch = """dip\((\d+)\)""".toRegex().find(dimension)
                if (dipMatch != null) {
                    "${dipMatch.groupValues[1]}.dp"
                } else {
                    "16.dp"
                }
            }
            else -> dimension
        }
    }
    
    override val priority: Int = 10
}