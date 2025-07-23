package com.sparkedember.ankotocompose

import kotlin.test.Test

class ConverterTest {

    @Test
    fun `test frameLayout conversion to Box`() {
        val ankoCode = """
            frameLayout {
                textView("Centered")
            }
        """.trimIndent()

        val result = convertAnkoToCompose(ankoCode)
        println("--- FRAME LAYOUT TEST RESULT ---")
        println(result)
        println("--- END FRAME LAYOUT TEST RESULT ---")

        // Check that frameLayout is converted to Box
        assert(result.contains("Box")) { "Expected 'Box' in result but got: $result" }
        assert(result.contains("Text(\"Centered\")")) { "Expected 'Text(\"Centered\")' in result but got: $result" }
    }

    @Test
    fun `test frameLayout with multiple children`() {
        val ankoCode = """
            frameLayout {
                textView("Background")
                button("Foreground") {
                    onClick { 
                        println("Clicked!")
                    }
                }
            }
        """.trimIndent()

        val result = convertAnkoToCompose(ankoCode)
        println("--- FRAME LAYOUT MULTIPLE CHILDREN TEST RESULT ---")
        println(result)
        println("--- END FRAME LAYOUT MULTIPLE CHILDREN TEST RESULT ---")

        // Check that frameLayout is converted to Box with multiple children
        assert(result.contains("Box")) { "Expected 'Box' in result but got: $result" }
        assert(result.contains("Text(\"Background\")")) { "Expected background text in result but got: $result" }
        assert(result.contains("Button")) { "Expected 'Button' in result but got: $result" }
        assert(result.contains("Text(\"Foreground\")")) { "Expected foreground button text in result but got: $result" }
    }

    @Test
    fun `test frameLayout with properties and lparams`() {
        val ankoCode = """
            frameLayout {
                backgroundColor = Color.parseColor("#FFEEEE")
                padding = dip(8)
                
                textView("Content")
            }.lparams(width = matchParent, height = wrapContent)
        """.trimIndent()

        val result = convertAnkoToCompose(ankoCode)
        println("--- FRAME LAYOUT WITH PROPERTIES TEST RESULT ---")
        println(result)
        println("--- END FRAME LAYOUT WITH PROPERTIES TEST RESULT ---")

        // Check that frameLayout is converted to Box with proper modifiers
        assert(result.contains("Box")) { "Expected 'Box' in result but got: $result" }
        assert(result.contains("modifier")) { "Expected 'modifier' in result but got: $result" }
        assert(result.contains("fillMaxWidth")) { "Expected 'fillMaxWidth' in result but got: $result" }
        assert(result.contains("wrapContentHeight")) { "Expected 'wrapContentHeight' in result but got: $result" }
        assert(result.contains("background")) { "Expected 'background' in result but got: $result" }
        assert(result.contains("padding")) { "Expected 'padding' in result but got: $result" }
        assert(result.contains("Text(\"Content\")")) { "Expected content text in result but got: $result" }
    }

    @Test
    fun `test frameLayout includes Box import`() {
        val ankoCode = """
            frameLayout {
                textView("Simple")
            }
        """.trimIndent()

        val result = convertAnkoToCompose(ankoCode)
        
        // Check that the necessary imports are included
        assert(result.contains("import androidx.compose.foundation.layout.Box")) { "Expected Box import in result but got: $result" }
        assert(result.contains("import androidx.compose.material.Text")) { "Expected Text import in result but got: $result" }
    }

    @Test
    fun `test coordinatorLayout conversion to Scaffold`() {
        val ankoCode = """
            coordinatorLayout {
                textView("Simple Content")
            }
        """.trimIndent()

        val result = convertAnkoToCompose(ankoCode)
        println("--- COORDINATOR LAYOUT TEST RESULT ---")
        println(result)
        println("--- END COORDINATOR LAYOUT TEST RESULT ---")

        // Check that coordinatorLayout is converted to Scaffold
        assert(result.contains("Scaffold")) { "Expected 'Scaffold' in result but got: $result" }
        assert(result.contains("paddingValues")) { "Expected 'paddingValues' in result but got: $result" }
        assert(result.contains("Text(\"Content\")")) { "Expected content text in result but got: $result" }
    }

    @Test
    fun `test coordinatorLayout with appBarLayout and FloatingActionButton`() {
        val ankoCode = """
            coordinatorLayout {
                appBarLayout {
                    toolbar {
                        title = "My App"
                    }
                }
                
                floatingActionButton {
                    onClick {
                        println("FAB clicked!")
                    }
                }
                
                verticalLayout {
                    textView("Main Content")
                    button("Action Button") {
                        onClick { println("Button clicked!") }
                    }
                }
            }
        """.trimIndent()

        val result = convertAnkoToCompose(ankoCode)
        println("--- COORDINATOR LAYOUT FULL TEST RESULT ---")
        println(result)
        println("--- END COORDINATOR LAYOUT FULL TEST RESULT ---")

        // Check that all components are properly converted
        assert(result.contains("Scaffold")) { "Expected 'Scaffold' in result but got: $result" }
        assert(result.contains("topBar")) { "Expected 'topBar' in result but got: $result" }
        assert(result.contains("TopAppBar")) { "Expected 'TopAppBar' in result but got: $result" }
        assert(result.contains("floatingActionButton")) { "Expected 'floatingActionButton' in result but got: $result" }
        assert(result.contains("FloatingActionButton")) { "Expected 'FloatingActionButton' in result but got: $result" }
        assert(result.contains("Text(\"My App\")")) { "Expected app title in result but got: $result" }
        assert(result.contains("Text(\"Main Content\")")) { "Expected main content in result but got: $result" }
    }

    @Test
    fun `test coordinatorLayout with only appBarLayout`() {
        val ankoCode = """
            coordinatorLayout {
                appBarLayout {
                    toolbar {
                        title = "Simple App"
                    }
                }
                
                verticalLayout {
                    textView("Content without FAB")
                }
            }
        """.trimIndent()

        val result = convertAnkoToCompose(ankoCode)
        println("--- COORDINATOR LAYOUT APP BAR ONLY TEST RESULT ---")
        println(result)
        println("--- END COORDINATOR LAYOUT APP BAR ONLY TEST RESULT ---")

        // Check that Scaffold with only topBar is created
        assert(result.contains("Scaffold")) { "Expected 'Scaffold' in result but got: $result" }
        assert(result.contains("topBar")) { "Expected 'topBar' in result but got: $result" }
        assert(result.contains("TopAppBar")) { "Expected 'TopAppBar' in result but got: $result" }
        assert(!result.contains("floatingActionButton =")) { "Expected no FAB property in result but got: $result" }
        assert(result.contains("Text(\"Main Content\")")) { "Expected main content in result but got: $result" }
    }

    @Test
    fun `test coordinatorLayout includes Scaffold imports`() {
        val ankoCode = """
            coordinatorLayout {
                textView("Test")
            }
        """.trimIndent()

        val result = convertAnkoToCompose(ankoCode)
        
        // Check that the necessary Scaffold imports are included
        assert(result.contains("import androidx.compose.material.Scaffold")) { "Expected Scaffold import in result but got: $result" }
        assert(result.contains("import androidx.compose.material.TopAppBar")) { "Expected TopAppBar import in result but got: $result" }
        assert(result.contains("import androidx.compose.material.FloatingActionButton")) { "Expected FloatingActionButton import in result but got: $result" }
    }
}