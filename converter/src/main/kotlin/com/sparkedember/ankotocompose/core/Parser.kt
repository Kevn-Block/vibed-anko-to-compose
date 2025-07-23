package com.sparkedember.ankotocompose.core

import org.jetbrains.kotlin.psi.KtFile

/**
 * Interface for parsing Anko code into an abstract representation
 */
interface Parser {
    /**
     * Parse Anko code into a structured representation
     * @param ankoCode The Anko code to parse
     * @return Parsed representation of the code
     * @throws ParseException if parsing fails
     */
    fun parse(ankoCode: String): ParseResult
}

/**
 * Result of parsing operation
 */
data class ParseResult(
    val layoutTree: LayoutNode,
    val customFunctions: Map<String, String> = emptyMap(),
    val imports: Set<String> = emptySet(),
    val psiFile: KtFile? = null
)

/**
 * Abstract representation of a layout node
 */
data class LayoutNode(
    val type: String,
    val properties: Map<String, Any> = emptyMap(),
    val children: List<LayoutNode> = emptyList(),
    val layoutParams: Map<String, Any> = emptyMap(),
    val lambdaBody: String? = null
)

/**
 * Exception thrown when parsing fails
 */
class ParseException(
    message: String,
    cause: Throwable? = null,
    val line: Int? = null,
    val column: Int? = null
) : Exception(message, cause)