// Test file to check if we can convert the toolbar layout portion

import com.sparkedember.ankotocompose.convertAnkoToCompose
import com.sparkedember.ankotocompose.factory.ConverterFactory

fun main() {
    val toolbarLayoutCode = """
        linearLayout {
            verticalLayout {
                textView {
                    text = "Title"
                    textSize = 16f
                    textColor = Color.BLACK
                    lines = 1
                    ellipsize = TextUtils.TruncateAt.END
                }
                textView {
                    textSize = 12f
                    textColor = Color.GRAY
                    visibility = View.GONE
                    lines = 1
                    ellipsize = TextUtils.TruncateAt.END
                }.lparams {
                    topMargin = dip(4)
                }
            }.lparams {
                width = 0
                weight = 1f
                marginEnd = dip(8)
                gravity = Gravity.CENTER_VERTICAL
            }
            frameLayout {
                // Action buttons would go here
            }.lparams {
                marginEnd = dip(16)
                width = ViewGroup.LayoutParams.WRAP_CONTENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                gravity = Gravity.CENTER_VERTICAL
            }
        }.lparams(width = ViewGroup.LayoutParams.MATCH_PARENT)
    """.trimIndent()

    println("Original Anko layout code:")
    println(toolbarLayoutCode)
    println("\n" + "=".repeat(50))
    
    // Test with old converter
    println("\nUsing original converter:")
    try {
        val oldResult = convertAnkoToCompose(toolbarLayoutCode)
        println(oldResult)
    } catch (e: Exception) {
        println("Failed: ${e.message}")
    }
    
    println("\n" + "=".repeat(50))
    
    // Test with new architecture
    println("\nUsing new architecture:")
    try {
        val converter = ConverterFactory.createDefaultConverter()
        val newResult = converter.convert(toolbarLayoutCode)
        
        // Print imports
        println("Imports:")
        newResult.imports.sorted().forEach { import ->
            println("import $import")
        }
        
        println("\nCompose code:")
        println(newResult.composeCode)
        
        println("\nMetadata:")
        println("Strategy: ${newResult.metadata.conversionStrategy}")
        if (newResult.warnings.isNotEmpty()) {
            println("Warnings:")
            newResult.warnings.forEach { warning ->
                println("  - $warning")
            }
        }
        
    } catch (e: Exception) {
        println("Failed: ${e.message}")
        e.printStackTrace()
    }
}