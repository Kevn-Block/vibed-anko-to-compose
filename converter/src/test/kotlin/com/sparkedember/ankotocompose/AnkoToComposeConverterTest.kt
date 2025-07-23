package com.sparkedember.ankotocompose

import kotlin.test.Test
import kotlin.test.assertEquals

class AnkoToComposeConverterTest {

    @Test
    fun testConvertsHorizontalLinearLayoutToRow() {
        val ankoCode = """
            linearLayout {
                orientation = LinearLayout.HORIZONTAL
                padding = dip(8)
                textView("Left")
                textView("Right")
            }
        """.trimIndent()

        val result = convertAnkoToCompose(ankoCode)

        // Check that linearLayout with horizontal orientation is converted to Row
        assert(result.contains("Row("))
        assert(result.contains("modifier = Modifier.padding(8.dp)"))
        assert(result.contains("Text(\"Left\")"))
        assert(result.contains("Text(\"Right\")"))
    }

    @Test
    fun testConvertsVerticalLinearLayoutToColumn() {
        val ankoCode = """
            linearLayout {
                // No orientation specified defaults to vertical
                textView("Top")
                textView("Bottom")
            }
        """.trimIndent()

        val result = convertAnkoToCompose(ankoCode)

        // Check that linearLayout without orientation is converted to Column
        assert(result.contains("Column {"))
        assert(result.contains("Text(\"Top\")"))
        assert(result.contains("Text(\"Bottom\")"))
    }

    @Test
    fun testConvertsImageViewToImage() {
        val ankoCode = """
            imageView {
                imageResource = android.R.drawable.sym_def_app_icon
            }.lparams(width = dip(80))
        """.trimIndent()

        val result = convertAnkoToCompose(ankoCode)

        // Check that imageView is converted to Image
        assert(result.contains("Image("))
        assert(result.contains("painter = painterResource(android.R.drawable.sym_def_app_icon)"))
        assert(result.contains("contentDescription = null"))
        assert(result.contains("modifier = Modifier.width(80.dp)"))
    }

    @Test
    fun testConvertsViewToDivider() {
        val ankoCode = """
            view {
                backgroundColor = Color.LTGRAY
            }.lparams(width = matchParent, height = dip(1))
        """.trimIndent()

        val result = convertAnkoToCompose(ankoCode)

        // Check that view with height=1dp is converted to Divider
        assert(result.contains("Divider("))
        assert(result.contains("color = Color.LightGray"))
        assert(result.contains("modifier = Modifier"))
        assert(result.contains(".fillMaxWidth()"))
        assert(result.contains(".height(1.dp)"))
    }

    @Test
    fun testConvertsScrollViewToColumnWithVerticalScroll() {
        val ankoCode = """
            scrollView {
                verticalLayout {
                    padding = dip(16)
                    textView("Top Content")
                    textView("Bottom Content")
                }
            }
        """.trimIndent()

        val result = convertAnkoToCompose(ankoCode)

        // Check that scrollView is converted to Column with verticalScroll
        assert(result.contains("Column("))
        assert(result.contains("modifier = Modifier.verticalScroll(rememberScrollState())"))
        assert(result.contains("Column("))
        assert(result.contains("modifier = Modifier.padding(16.dp)"))
        assert(result.contains("Text(\"Top Content\")"))
        assert(result.contains("Text(\"Bottom Content\")"))
    }

    @Test
    fun testConvertsCheckBoxToRowWithCheckboxAndText() {
        val ankoCode = """
            checkBox {
                text = "Accept Terms"
                isChecked = true
            }.lparams { topMargin = dip(8) }
        """.trimIndent()

        val result = convertAnkoToCompose(ankoCode)

        // Check that checkBox is converted to Row with Checkbox and Text
        assert(result.contains("Row("))
        assert(result.contains("modifier = Modifier.padding(top = 8.dp)"))
        assert(result.contains("verticalAlignment = Alignment.CenterVertically"))
        assert(result.contains("Checkbox("))
        assert(result.contains("checked = true"))
        assert(result.contains("onCheckedChange = { /* TODO: Handle state change */ }"))
        assert(result.contains("Text("))
        assert(result.contains("text = \"Accept Terms\""))
        assert(result.contains("modifier = Modifier.padding(start = 8.dp)"))
    }

    @Test
    fun testConvertsSwitchToRowWithSwitchAndText() {
        val ankoCode = """
            switch {
                text = "Enable Notifications"
                isChecked = false
            }
        """.trimIndent()

        val result = convertAnkoToCompose(ankoCode)

        // Check that switch is converted to Row with Switch and Text
        assert(result.contains("Row("))
        assert(result.contains("verticalAlignment = Alignment.CenterVertically"))
        assert(result.contains("Switch("))
        assert(result.contains("checked = false"))
        assert(result.contains("onCheckedChange = { /* TODO: Handle state change */ }"))
        assert(result.contains("Text("))
        assert(result.contains("text = \"Enable Notifications\""))
        assert(result.contains("modifier = Modifier.padding(start = 8.dp)"))
    }

    @Test
    fun testConvertsCoordinatorLayoutWithCollapsingToolbar() {
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

        val result = convertAnkoToCompose(ankoCode)

        // Check that the collapsing toolbar is properly converted
        assert(result.contains("val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())"))
        assert(result.contains("Scaffold("))
        assert(result.contains("modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)"))
        assert(result.contains("LargeTopAppBar("))
        assert(result.contains("title = { Text(\"My App Title\") }"))
        assert(result.contains("scrollBehavior = scrollBehavior"))
        assert(result.contains("floatingActionButton = {"))
        assert(result.contains("FloatingActionButton("))
        assert(result.contains("Icon(Icons.Filled.Add, contentDescription = \"Add\")"))
        assert(result.contains("Column(modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState()))"))
        assert(result.contains("Text(\"Scrollable content...\")"))
    }
}
