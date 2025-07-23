package com.sparkedember.ankotocompose.impl

import com.sparkedember.ankotocompose.core.LayoutNode
import com.sparkedember.ankotocompose.core.LayoutTransformer
import com.sparkedember.ankotocompose.core.TransformerRegistry

/**
 * Default implementation of TransformerRegistry
 */
class DefaultTransformerRegistry : TransformerRegistry {
    private val transformers = mutableListOf<LayoutTransformer>()
    
    override fun register(transformer: LayoutTransformer) {
        transformers.add(transformer)
        // Sort by priority (higher priority first)
        transformers.sortByDescending { it.priority }
    }
    
    override fun getTransformers(): List<LayoutTransformer> = transformers.toList()
    
    override fun findTransformer(node: LayoutNode): LayoutTransformer? {
        return transformers.firstOrNull { it.canTransform(node) }
    }
}