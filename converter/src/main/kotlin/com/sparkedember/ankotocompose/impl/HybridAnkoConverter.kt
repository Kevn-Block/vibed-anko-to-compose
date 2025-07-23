package com.sparkedember.ankotocompose.impl

import com.sparkedember.ankotocompose.core.*
import com.sparkedember.ankotocompose.config.ComposeImports
import com.sparkedember.ankotocompose.parsing.AstParser
import com.sparkedember.ankotocompose.parsing.PatternMatchingParser

/**
 * Hybrid converter that tries AST parsing first, then falls back to pattern matching
 */
class HybridAnkoConverter(
    private val astParser: AstParser = AstParser(),
    private val patternParser: PatternMatchingParser = PatternMatchingParser(),
    private val transformerRegistry: TransformerRegistry = DefaultTransformerRegistry()
) : AnkoConverter {
    
    override fun convert(ankoCode: String): ConversionResult {
        return try {
            convertWithAst(ankoCode)
        } catch (e: Exception) {
            convertWithPatternMatching(ankoCode)
        }
    }
    
    private fun convertWithAst(ankoCode: String): ConversionResult {
        try {
            val parseResult = astParser.parse(ankoCode)
            val context = TransformationContext(
                availableTransformers = transformerRegistry.getTransformers(),
                imports = mutableSetOf(),
                customFunctions = parseResult.customFunctions
            )
            
            val transformationResult = transformLayout(parseResult.layoutTree, context)
            
            // Add custom functions to the result
            val customFunctionsCode = parseResult.customFunctions.values.joinToString("\n\n")
            val finalCode = if (customFunctionsCode.isNotEmpty()) {
                "$customFunctionsCode\n\n${transformationResult.code}"
            } else {
                transformationResult.code
            }
            
            return ConversionResult(
                composeCode = finalCode,
                imports = context.imports + transformationResult.requiredImports + ComposeImports.BASIC_IMPORTS,
                warnings = transformationResult.warnings,
                metadata = ConversionMetadata(
                    conversionStrategy = ConversionStrategy.AST_PARSING,
                    customFunctionsFound = parseResult.customFunctions.keys,
                    transformersUsed = emptySet() // TODO: Track this
                )
            )
        } catch (e: Exception) {
            throw ConversionException("AST parsing failed", e, ankoCode)
        }
    }
    
    private fun convertWithPatternMatching(ankoCode: String): ConversionResult {
        val parseResult = patternParser.parse(ankoCode)
        val context = TransformationContext(
            availableTransformers = transformerRegistry.getTransformers(),
            imports = mutableSetOf()
        )
        
        val transformationResult = transformLayout(parseResult.layoutTree, context)
        
        return ConversionResult(
            composeCode = transformationResult.code,
            imports = context.imports + transformationResult.requiredImports + ComposeImports.BASIC_IMPORTS,
            warnings = transformationResult.warnings + listOf("Used pattern matching fallback - some complex features may not be converted correctly"),
            metadata = ConversionMetadata(
                conversionStrategy = ConversionStrategy.PATTERN_MATCHING
            )
        )
    }
    
    private fun transformLayout(node: LayoutNode, context: TransformationContext): TransformationResult {
        val transformer = transformerRegistry.findTransformer(node)
            ?: throw ConversionException("No transformer found for layout type: ${node.type}")
        
        return transformer.transform(node, context)
    }
}