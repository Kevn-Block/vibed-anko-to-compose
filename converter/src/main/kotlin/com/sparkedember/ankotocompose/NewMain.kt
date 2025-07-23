package com.sparkedember.ankotocompose

import com.sparkedember.ankotocompose.factory.ConverterFactory

/**
 * New main function demonstrating the refactored converter
 */
fun main() {
    val ankoCode = """
        verticalLayout {
            backgroundColor = Color.parseColor("#EEEEEE")
            padding = dip(16)
            
            textView("Hello, Compose!")
        }.lparams(width = matchParent)
    """.trimIndent()

    println("Original Anko code:")
    println(ankoCode)
    println("\nConverted to Compose using new architecture:")

    try {
        val converter = ConverterFactory.createDefaultConverter()
        val result = converter.convert(ankoCode)
        
        // Print imports
        if (result.imports.isNotEmpty()) {
            result.imports.sorted().forEach { import ->
                println("import $import")
            }
            println()
        }
        
        // Print the converted code
        println(result.composeCode)
        
        // Print metadata
        println("\n// Conversion metadata:")
        println("// Strategy: ${result.metadata.conversionStrategy}")
        if (result.warnings.isNotEmpty()) {
            println("// Warnings:")
            result.warnings.forEach { warning ->
                println("//   - $warning")
            }
        }
        
    } catch (e: Exception) {
        println("Conversion failed: ${e.message}")
        e.printStackTrace()
    }
}

/**
 * Function to convert Anko to Compose using the new architecture
 * This maintains compatibility with existing tests
 */
fun convertAnkoToComposeNew(ankoCode: String): String {
    return try {
        val converter = ConverterFactory.createDefaultConverter()
        val result = converter.convert(ankoCode)
        
        // Format similar to old output for compatibility
        val imports = result.imports.sorted().joinToString("\n") { "import $it" }
        "$imports\n\n${result.composeCode}"
        
    } catch (e: Exception) {
        // Fallback to the old implementation for now
        convertAnkoToCompose(ankoCode)
    }
}