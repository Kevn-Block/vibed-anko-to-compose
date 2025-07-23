package com.sparkedember.ankotocompose.factory

import com.sparkedember.ankotocompose.core.AnkoConverter
import com.sparkedember.ankotocompose.core.TransformerRegistry
import com.sparkedember.ankotocompose.impl.DefaultTransformerRegistry
import com.sparkedember.ankotocompose.impl.HybridAnkoConverter
import com.sparkedember.ankotocompose.transformers.*

/**
 * Factory for creating configured AnkoConverter instances
 */
object ConverterFactory {
    
    /**
     * Create a fully configured AnkoConverter with all default transformers
     */
    fun createDefaultConverter(): AnkoConverter {
        val registry = createDefaultTransformerRegistry()
        return HybridAnkoConverter(transformerRegistry = registry)
    }
    
    /**
     * Create a transformer registry with all default transformers registered
     */
    fun createDefaultTransformerRegistry(): TransformerRegistry {
        val registry = DefaultTransformerRegistry()
        
        // Register transformers in order of priority
        registry.register(CoordinatorLayoutWithCollapsingTransformer())
        registry.register(RowTransformer())
        registry.register(ColumnTransformer())
        registry.register(BoxTransformer())
        registry.register(TextViewTransformer())
        // Add other transformers as they are created...
        
        return registry
    }
    
    /**
     * Create a minimal converter for testing purposes
     */
    fun createMinimalConverter(): AnkoConverter {
        val registry = DefaultTransformerRegistry()
        registry.register(TextViewTransformer())
        registry.register(ColumnTransformer())
        
        return HybridAnkoConverter(transformerRegistry = registry)
    }
}