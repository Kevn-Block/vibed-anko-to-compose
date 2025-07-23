package com.sparkedember.ankotocompose.transformers

import com.sparkedember.ankotocompose.core.*
import com.sparkedember.ankotocompose.config.ComposeImports

/**
 * Transformer for CoordinatorLayout with collapsing toolbar
 */
class CoordinatorLayoutWithCollapsingTransformer : LayoutTransformer {
    
    override fun canTransform(node: LayoutNode): Boolean {
        return node.type == "coordinatorLayoutWithCollapsing"
    }
    
    override fun transform(node: LayoutNode, context: TransformationContext): TransformationResult {
        val requiredImports = mutableSetOf<String>()
        
        // Add scaffold imports
        requiredImports.addAll(ComposeImports.SCAFFOLD_IMPORTS)
        requiredImports.add(ComposeImports.MODIFIER)
        requiredImports.add(ComposeImports.PADDING)
        requiredImports.add(ComposeImports.VERTICAL_SCROLL)
        requiredImports.add(ComposeImports.REMEMBER_SCROLL_STATE)
        requiredImports.add(ComposeImports.TEXT)
        requiredImports.add(ComposeImports.COLUMN)
        requiredImports.add(ComposeImports.ICON)
        requiredImports.add(ComposeImports.ICONS_FILLED_ADD)
        
        // Extract title from properties
        val title = node.properties["title"]?.toString()?.removeSurrounding("\"") ?: "App Title"
        
        // Extract content from children
        val contentNodes = node.children.filter { it.type != "floatingActionButton" }
        val contentCode = if (contentNodes.isNotEmpty()) {
            val childrenCode = contentNodes.map { child ->
                when (child.type) {
                    "textView" -> {
                        val text = child.properties["text"]?.toString()?.removeSurrounding("\"") ?: "Text"
                        "Text(\"$text\")"
                    }
                    else -> "// TODO: Convert ${child.type}"
                }
            }.joinToString("\n        ")
            
            "Column(modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState())) {\n        $childrenCode\n    }"
        } else {
            "// Content goes here"
        }
        
        val code = """
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    LargeTopAppBar(
                        title = { Text("$title") },
                        scrollBehavior = scrollBehavior
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add")
                    }
                }
            ) { innerPadding ->
                $contentCode
            }
        """.trimIndent()
        
        return TransformationResult(
            code = code,
            requiredImports = requiredImports
        )
    }
    
    override val priority: Int = 20 // Higher priority than regular coordinator layout
}