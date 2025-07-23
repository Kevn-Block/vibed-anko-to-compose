package com.sparkedember.ankotocompose

import com.sparkedember.ankotocompose.factory.ConverterFactory
import com.sparkedember.ankotocompose.core.ConversionStrategy
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class NewArchitectureTest {

    @Test
    fun testNewArchitectureBasicConversion() {
        val ankoCode = """
            verticalLayout {
                textView("Hello, World!")
            }
        """.trimIndent()

        val converter = ConverterFactory.createDefaultConverter()
        val result = converter.convert(ankoCode)

        // Debug output removed

        // Check that conversion worked
        assertTrue(result.composeCode.contains("Column"))
        assertTrue(result.composeCode.contains("Text(\"Hello, World!\")"))
        
        // Check that imports are included
        assertTrue(result.imports.contains("androidx.compose.foundation.layout.Column"))
        assertTrue(result.imports.contains("androidx.compose.material.Text"))
    }

    @Test
    fun testNewArchitectureCollapsingToolbar() {
        val ankoCode = """
            coordinatorLayout {
                fitsSystemWindows = true

                appBarLayout {
                    fitsSystemWindows = true

                    collapsingToolbarLayout {
                        fitsSystemWindows = true
                        title = "My App Title"

                        toolbar {
                            // Toolbar properties
                        }.lparams(width = matchParent, height = dip(56))

                    }.lparams(width = matchParent, height = matchParent)

                }.lparams(width = matchParent, height = dip(200))

                nestedScrollView {
                    textView("Scrollable content...")
                }

                floatingActionButton {
                    imageResource = android.R.drawable.ic_input_add
                }
            }
        """.trimIndent()

        val converter = ConverterFactory.createDefaultConverter()
        val result = converter.convert(ankoCode)

        // Should use pattern matching since AST parsing would fail on this complex structure
        assertEquals(ConversionStrategy.PATTERN_MATCHING, result.metadata.conversionStrategy)
        
        // Check for scaffold components
        assertTrue(result.composeCode.contains("Scaffold"))
        assertTrue(result.composeCode.contains("LargeTopAppBar"))
        assertTrue(result.composeCode.contains("My App Title"))
        assertTrue(result.composeCode.contains("scrollBehavior"))
    }

    @Test 
    fun testConverterFactoryMinimal() {
        val ankoCode = "textView(\"Test\")"
        
        val converter = ConverterFactory.createMinimalConverter()
        val result = converter.convert(ankoCode)
        
        assertTrue(result.composeCode.contains("Text(\"Test\")"))
    }

    @Test
    fun testBackwardCompatibility() {
        val ankoCode = """
            verticalLayout {
                textView("Hello")
            }
        """.trimIndent()

        // Test that the new function produces reasonable output
        val newResult = convertAnkoToComposeNew(ankoCode)
        
        assertTrue(newResult.contains("import"))
        assertTrue(newResult.contains("Column"))
        assertTrue(newResult.contains("Text"))
    }
}