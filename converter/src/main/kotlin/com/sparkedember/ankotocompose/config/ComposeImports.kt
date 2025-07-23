package com.sparkedember.ankotocompose.config

/**
 * Constants for Jetpack Compose imports
 */
object ComposeImports {
    // Layout imports
    const val COLUMN = "androidx.compose.foundation.layout.Column"
    const val ROW = "androidx.compose.foundation.layout.Row"
    const val BOX = "androidx.compose.foundation.layout.Box"
    const val SCAFFOLD = "androidx.compose.material.Scaffold"
    
    // Material components
    const val TEXT = "androidx.compose.material.Text"
    const val BUTTON = "androidx.compose.material.Button"
    const val TOP_APP_BAR = "androidx.compose.material.TopAppBar"
    const val LARGE_TOP_APP_BAR = "androidx.compose.material3.LargeTopAppBar"
    const val FLOATING_ACTION_BUTTON = "androidx.compose.material.FloatingActionButton"
    const val CHECKBOX = "androidx.compose.material.Checkbox"
    const val SWITCH = "androidx.compose.material.Switch"
    const val DIVIDER = "androidx.compose.material.Divider"
    const val IMAGE = "androidx.compose.foundation.Image"
    
    // Modifiers
    const val MODIFIER = "androidx.compose.ui.Modifier"
    const val PADDING = "androidx.compose.foundation.layout.padding"
    const val FILL_MAX_WIDTH = "androidx.compose.foundation.layout.fillMaxWidth"
    const val FILL_MAX_HEIGHT = "androidx.compose.foundation.layout.fillMaxHeight"
    const val WRAP_CONTENT_WIDTH = "androidx.compose.foundation.layout.wrapContentWidth"
    const val WRAP_CONTENT_HEIGHT = "androidx.compose.foundation.layout.wrapContentHeight"
    const val WEIGHT = "androidx.compose.foundation.layout.weight"
    const val WIDTH = "androidx.compose.foundation.layout.width"
    const val HEIGHT = "androidx.compose.foundation.layout.height"
    const val BACKGROUND = "androidx.compose.foundation.background"
    
    // Scroll behavior
    const val VERTICAL_SCROLL = "androidx.compose.foundation.verticalScroll"
    const val REMEMBER_SCROLL_STATE = "androidx.compose.foundation.rememberScrollState"
    const val NESTED_SCROLL = "androidx.compose.ui.input.nestedscroll.nestedScroll"
    const val TOP_APP_BAR_DEFAULTS = "androidx.compose.material3.TopAppBarDefaults"
    const val REMEMBER_TOP_APP_BAR_STATE = "androidx.compose.material3.rememberTopAppBarState"
    
    // Other
    const val DP = "androidx.compose.ui.unit.dp"
    const val SP = "androidx.compose.ui.unit.sp"
    const val COLOR = "androidx.compose.ui.graphics.Color"
    const val ALIGNMENT = "androidx.compose.ui.Alignment"
    const val COMPOSABLE = "androidx.compose.runtime.Composable"
    const val PAINTER_RESOURCE = "androidx.compose.ui.res.painterResource"
    const val ICONS = "androidx.compose.material.icons.Icons"
    const val ICONS_FILLED_ADD = "androidx.compose.material.icons.filled.Add"
    const val ICON = "androidx.compose.material.Icon"
    const val LAZY_COLUMN = "androidx.compose.foundation.lazy.LazyColumn"
    
    /**
     * Get all basic imports needed for most conversions
     */
    val BASIC_IMPORTS = setOf(
        COLUMN, ROW, TEXT, MODIFIER, PADDING, DP, COLOR
    )
    
    /**
     * Get all layout-related imports
     */
    val LAYOUT_IMPORTS = setOf(
        COLUMN, ROW, BOX, FILL_MAX_WIDTH, FILL_MAX_HEIGHT,
        WRAP_CONTENT_WIDTH, WRAP_CONTENT_HEIGHT, WEIGHT, WIDTH, HEIGHT
    )
    
    /**
     * Get all material component imports
     */
    val MATERIAL_IMPORTS = setOf(
        TEXT, BUTTON, CHECKBOX, SWITCH, DIVIDER, IMAGE,
        FLOATING_ACTION_BUTTON, ICON, ICONS, ICONS_FILLED_ADD
    )
    
    /**
     * Get scaffold-related imports
     */
    val SCAFFOLD_IMPORTS = setOf(
        SCAFFOLD, TOP_APP_BAR, LARGE_TOP_APP_BAR, FLOATING_ACTION_BUTTON,
        TOP_APP_BAR_DEFAULTS, REMEMBER_TOP_APP_BAR_STATE, NESTED_SCROLL
    )
}