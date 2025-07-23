package com.sparkedember.ankotocompose.config

/**
 * Mappings from Anko components to Compose components
 */
object AnkoMappings {
    /**
     * Anko layout functions to Compose equivalents
     */
    val LAYOUT_MAPPINGS = mapOf(
        "verticalLayout" to "Column",
        "linearLayout" to "Column", // Default, can be overridden to Row based on orientation
        "frameLayout" to "Box",
        "scrollView" to "Column", // With scroll modifier
        "coordinatorLayout" to "Scaffold"
    )
    
    /**
     * Anko widget functions to Compose equivalents
     */
    val WIDGET_MAPPINGS = mapOf(
        "textView" to "Text",
        "button" to "Button",
        "imageView" to "Image",
        "checkBox" to "Checkbox",
        "switch" to "Switch",
        "view" to "Divider" // When used as divider
    )
    
    /**
     * Anko property names to Compose modifier functions
     */
    val PROPERTY_MAPPINGS = mapOf(
        "backgroundColor" to "background",
        "padding" to "padding",
        "textSize" to "fontSize"
    )
    
    /**
     * Anko layout parameter names to Compose modifiers
     */
    val LAYOUT_PARAM_MAPPINGS = mapOf(
        "width" to mapOf(
            "matchParent" to "fillMaxWidth",
            "wrapContent" to "wrapContentWidth"
        ),
        "height" to mapOf(
            "matchParent" to "fillMaxHeight", 
            "wrapContent" to "wrapContentHeight"
        ),
        "weight" to "weight",
        "margin" to "padding",
        "topMargin" to "padding(top = ...)",
        "bottomMargin" to "padding(bottom = ...)",
        "leftMargin" to "padding(start = ...)",
        "rightMargin" to "padding(end = ...)"
    )
    
    /**
     * Color mappings from Android to Compose
     */
    val COLOR_MAPPINGS = mapOf(
        "Color.LTGRAY" to "Color.LightGray",
        "Color.GRAY" to "Color.Gray",
        "Color.DKGRAY" to "Color.DarkGray",
        "Color.BLACK" to "Color.Black",
        "Color.WHITE" to "Color.White",
        "Color.RED" to "Color.Red",
        "Color.GREEN" to "Color.Green",
        "Color.BLUE" to "Color.Blue"
    )
}