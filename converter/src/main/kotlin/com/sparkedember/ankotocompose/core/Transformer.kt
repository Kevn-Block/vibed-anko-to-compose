package com.sparkedember.ankotocompose.core

/**
 * Interface for transforming parsed layout nodes into Compose code
 */
interface LayoutTransformer {
    /**
     * Check if this transformer can handle the given layout node
     */
    fun canTransform(node: LayoutNode): Boolean
    
    /**
     * Transform a layout node into Compose code
     */
    fun transform(node: LayoutNode, context: TransformationContext): TransformationResult
    
    /**
     * Priority of this transformer (higher priority transformers are tried first)
     */
    val priority: Int get() = 0
}

/**
 * Context for transformation operations
 */
data class TransformationContext(
    val availableTransformers: List<LayoutTransformer>,
    val imports: MutableSet<String>,
    val customFunctions: Map<String, String> = emptyMap(),
    val depth: Int = 0
) {
    fun withIncreasedDepth(): TransformationContext = copy(depth = depth + 1)
}

/**
 * Result of a transformation operation
 */
data class TransformationResult(
    val code: String,
    val requiredImports: Set<String> = emptySet(),
    val warnings: List<String> = emptyList()
)

/**
 * Registry for managing layout transformers
 */
interface TransformerRegistry {
    fun register(transformer: LayoutTransformer)
    fun getTransformers(): List<LayoutTransformer>
    fun findTransformer(node: LayoutNode): LayoutTransformer?
}