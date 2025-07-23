package com.sparkedember.ankotocompose.parsing

import com.sparkedember.ankotocompose.core.*
import com.sparkedember.ankotocompose.config.AnkoMappings

/**
 * Simple pattern-matching parser for straightforward Anko conversions
 */
class PatternMatchingParser : Parser {
    
    override fun parse(ankoCode: String): ParseResult {
        val layoutType = extractMainLayoutType(ankoCode)
        val properties = extractProperties(ankoCode)
        val children = extractChildren(ankoCode)
        
        val rootNode = LayoutNode(
            type = layoutType,
            properties = properties,
            children = children
        )
        
        return ParseResult(
            layoutTree = rootNode,
            imports = extractImports(ankoCode)
        )
    }
    
    private fun extractMainLayoutType(ankoCode: String): String {
        val trimmed = ankoCode.trim()
        
        return when {
            trimmed.startsWith("coordinatorLayout") -> {
                if (ankoCode.contains("collapsingToolbarLayout")) {
                    "coordinatorLayoutWithCollapsing"
                } else {
                    "coordinatorLayout"
                }
            }
            trimmed.startsWith("verticalLayout") -> "verticalLayout"
            trimmed.startsWith("linearLayout") -> {
                if (ankoCode.contains("orientation = LinearLayout.HORIZONTAL")) {
                    "horizontalLinearLayout"
                } else {
                    "verticalLinearLayout"
                }
            }
            trimmed.startsWith("frameLayout") -> "frameLayout"
            trimmed.startsWith("scrollView") -> "scrollView"
            trimmed.startsWith("textView") -> "textView"
            trimmed.startsWith("button") -> "button"
            trimmed.startsWith("imageView") -> "imageView"
            trimmed.startsWith("checkBox") -> "checkBox"
            trimmed.startsWith("switch") -> "switch"
            trimmed.startsWith("view") && ankoCode.contains("height = dip(1)") -> "divider"
            else -> "unknown"
        }
    }
    
    private fun extractProperties(ankoCode: String): Map<String, Any> {
        val properties = mutableMapOf<String, Any>()
        
        // Extract common properties
        extractProperty(ankoCode, "backgroundColor", properties)
        extractProperty(ankoCode, "padding", properties)
        extractProperty(ankoCode, "textSize", properties)
        extractProperty(ankoCode, "title", properties)
        extractProperty(ankoCode, "text", properties)
        extractProperty(ankoCode, "imageResource", properties)
        extractProperty(ankoCode, "isChecked", properties)
        extractProperty(ankoCode, "orientation", properties)
        
        return properties
    }
    
    private fun extractProperty(ankoCode: String, propertyName: String, properties: MutableMap<String, Any>) {
        val regex = """$propertyName\s*=\s*([^\\n,}]+)""".toRegex()
        val match = regex.find(ankoCode)
        if (match != null) {
            properties[propertyName] = match.groupValues[1].trim()
        }
    }
    
    private fun extractChildren(ankoCode: String): List<LayoutNode> {
        val children = mutableListOf<LayoutNode>()
        
        // Look for all function calls that could be child layouts
        val childPatterns = listOf(
            "textView",
            "button", 
            "verticalLayout",
            "linearLayout",
            "frameLayout",
            "scrollView",
            "imageView",
            "checkBox",
            "switch",
            "floatingActionButton"
        )
        
        childPatterns.forEach { pattern ->
            // Look for pattern followed by either ("text") or { ... }
            val regex = """$pattern(?:\("([^"]*)"\)|\s*\{[^}]*\})""".toRegex()
            val matches = regex.findAll(ankoCode)
            
            for (match in matches) {
                val properties = mutableMapOf<String, Any>()
                
                // If there's a text argument, add it
                if (match.groupValues.size > 1 && match.groupValues[1].isNotEmpty()) {
                    properties["text"] = match.groupValues[1]
                }
                
                children.add(LayoutNode(pattern, properties))
            }
        }
        
        return children
    }
    
    private fun extractImports(ankoCode: String): Set<String> {
        // For pattern matching, we'll determine imports based on what we find
        return emptySet()
    }
}