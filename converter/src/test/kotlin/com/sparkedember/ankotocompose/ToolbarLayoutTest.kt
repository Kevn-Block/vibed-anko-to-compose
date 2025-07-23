package com.sparkedember.ankotocompose

import com.sparkedember.ankotocompose.factory.ConverterFactory
import com.sparkedember.ankotocompose.core.ConversionStrategy
import kotlin.test.Test
import kotlin.test.assertTrue

class ToolbarLayoutTest {

    @Test
    fun testSimpleNestedLayout() {
        val ankoCode = """
            linearLayout {
                textView("Title")
                textView("Subtitle") 
            }
        """.trimIndent()

        val converter = ConverterFactory.createDefaultConverter()
        val result = converter.convert(ankoCode)

        println("Result code: ${result.composeCode}")
        println("Strategy: ${result.metadata.conversionStrategy}")
        println("Warnings: ${result.warnings}")

        // Should contain both text views
        assertTrue(result.composeCode.contains("Text(\"Title\")"))
        assertTrue(result.composeCode.contains("Text(\"Subtitle\")"))
    }

    @Test 
    fun testNestedLayoutWithFrameLayout() {
        val ankoCode = """
            linearLayout {
                verticalLayout {
                    textView("Title")
                    textView("Subtitle")
                }
                frameLayout {
                    // Actions
                }
            }
        """.trimIndent()

        val converter = ConverterFactory.createDefaultConverter()
        val result = converter.convert(ankoCode)

        println("Result code: ${result.composeCode}")
        println("Strategy: ${result.metadata.conversionStrategy}")

        // Should convert to Row with Column and Box
        assertTrue(result.composeCode.contains("Text(\"Title\")"))
        assertTrue(result.composeCode.contains("Text(\"Subtitle\")"))
    }
}