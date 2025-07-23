package com.sparkedember.ankotocompose.core

/**
 * Core interface for converting Anko DSL code to Jetpack Compose code
 */
interface AnkoConverter {
    /**
     * Convert Anko DSL code to Jetpack Compose code
     * @param ankoCode The Anko DSL code to convert
     * @return The converted Jetpack Compose code
     * @throws ConversionException if the conversion fails
     */
    fun convert(ankoCode: String): ConversionResult
}

/**
 * Result of a conversion operation
 */
data class ConversionResult(
    val composeCode: String,
    val imports: Set<String> = emptySet(),
    val warnings: List<String> = emptyList(),
    val metadata: ConversionMetadata = ConversionMetadata()
)

/**
 * Metadata about the conversion process
 */
data class ConversionMetadata(
    val conversionStrategy: ConversionStrategy = ConversionStrategy.UNKNOWN,
    val customFunctionsFound: Set<String> = emptySet(),
    val transformersUsed: Set<String> = emptySet()
)

/**
 * Strategy used for conversion
 */
enum class ConversionStrategy {
    AST_PARSING,
    PATTERN_MATCHING,
    HYBRID,
    UNKNOWN
}

/**
 * Exception thrown when conversion fails
 */
class ConversionException(
    message: String,
    cause: Throwable? = null,
    val originalCode: String? = null
) : Exception(message, cause)