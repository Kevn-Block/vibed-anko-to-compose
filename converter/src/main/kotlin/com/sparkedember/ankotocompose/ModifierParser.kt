package com.sparkedember.ankotocompose

import org.jetbrains.kotlin.psi.*

/**
 * Helper class to parse lparams calls and property assignments and convert them to Compose Modifier chains
 */
object ModifierParser {
    /**
     * Result of parsing property assignments from a layout's lambda body
     * @param modifierString The Compose Modifier string
     * @param processedStatements The statements that were processed and should be removed
     * @param isHorizontal Whether the layout should be horizontal (Row) instead of vertical (Column)
     */
    data class PropertyParseResult(
        val modifierString: String,
        val processedStatements: List<KtExpression>,
        val isHorizontal: Boolean = false
    )
    /**
     * Parse a lparams call and generate a Compose Modifier string
     * @param lparam The lparams call expression
     * @return The Compose Modifier string
     */
    fun parse(lparam: KtCallExpression): String {
        val modifiers = mutableListOf<String>()

        // Process value arguments (e.g., width = matchParent)
        for (arg in lparam.valueArguments) {
            val name = arg.getArgumentName()?.asName?.asString() ?: continue
            val expression = arg.getArgumentExpression()?.text ?: continue

            when (name) {
                "width" -> {
                    when (expression) {
                        "matchParent" -> modifiers.add(".fillMaxWidth()")
                        "wrapContent" -> modifiers.add(".wrapContentWidth()")
                    }
                }
                "height" -> {
                    when (expression) {
                        "matchParent" -> modifiers.add(".fillMaxHeight()")
                        "wrapContent" -> modifiers.add(".wrapContentHeight()")
                    }
                }
                "weight" -> {
                    modifiers.add(".weight($expression)")
                }
            }
        }

        // Process lambda argument (e.g., { topMargin = dip(8) })
        val lambdaArg = lparam.lambdaArguments.firstOrNull()?.getLambdaExpression()
        lambdaArg?.bodyExpression?.statements?.forEach { statement ->
            if (statement is KtBinaryExpression && statement.operationToken.toString() == "=") {
                val left = statement.left?.text ?: return@forEach
                val right = statement.right ?: return@forEach

                when (left) {
                    "topMargin" -> {
                        if (right is KtCallExpression && right.calleeExpression?.text == "dip") {
                            val dipValue = right.valueArguments.firstOrNull()?.getArgumentExpression()?.text ?: "0"
                            modifiers.add(".padding(top = ${dipValue}.dp)")
                        }
                    }
                    "margin" -> {
                        if (right is KtCallExpression && right.calleeExpression?.text == "dip") {
                            val dipValue = right.valueArguments.firstOrNull()?.getArgumentExpression()?.text ?: "0"
                            modifiers.add(".padding(${dipValue}.dp)")
                        }
                    }
                    "weight" -> {
                        modifiers.add(".weight(${right.text})")
                    }
                }
            }
        }

        // If no modifiers were found, return an empty modifier
        if (modifiers.isEmpty()) {
            return "Modifier"
        }

        // Construct the modifier chain
        return "Modifier" + modifiers.joinToString("")
    }

    /**
     * Parse property assignments from a layout's lambda body and generate a Compose Modifier string
     * @param blockExpression The body of the layout's lambda
     * @return A PropertyParseResult containing the Modifier string, processed statements, and isHorizontal flag
     */
    fun parsePropertyAssignments(blockExpression: KtBlockExpression): PropertyParseResult {
        val modifiers = mutableListOf<String>()
        val processedStatements = mutableListOf<KtExpression>()
        var isHorizontal = false

        // Process statements in the block
        blockExpression.statements.forEach { statement ->
            if (statement is KtBinaryExpression && statement.operationToken.toString() == "=") {
                val left = statement.left?.text ?: return@forEach
                val right = statement.right ?: return@forEach

                when (left) {
                    "padding" -> {
                        if (right is KtCallExpression && right.calleeExpression?.text == "dip") {
                            val dipValue = right.valueArguments.firstOrNull()?.getArgumentExpression()?.text ?: "0"
                            modifiers.add(".padding(${dipValue}.dp)")
                            processedStatements.add(statement)
                        }
                    }
                    "backgroundColor" -> {
                        if (right is KtDotQualifiedExpression) {
                            // Handle Color.WHITE, Color.RED, etc.
                            val receiver = right.receiverExpression.text
                            val selector = right.selectorExpression?.text ?: ""

                            if (receiver == "Color") {
                                // Map Android Color constants to Compose Color constants
                                val composeColor = when (selector) {
                                    "WHITE" -> "Color.White"
                                    "BLACK" -> "Color.Black"
                                    "RED" -> "Color.Red"
                                    "GREEN" -> "Color.Green"
                                    "BLUE" -> "Color.Blue"
                                    "YELLOW" -> "Color.Yellow"
                                    "GRAY" -> "Color.Gray"
                                    else -> "Color.Gray" // Default fallback
                                }
                                modifiers.add(".background($composeColor)")
                                processedStatements.add(statement)
                            }
                        } else if (right is KtCallExpression && 
                                  right.calleeExpression is KtDotQualifiedExpression) {
                            // Handle Color.parseColor("#EEEEEE")
                            val dotExpr = right.calleeExpression as KtDotQualifiedExpression
                            if (dotExpr.receiverExpression.text == "Color" && 
                                dotExpr.selectorExpression?.text == "parseColor") {

                                val colorArg = right.valueArguments.firstOrNull()?.
                                    getArgumentExpression()?.text ?: return@forEach

                                // Convert "#EEEEEE" to "0xFFEEEEEE"
                                val hexColor = if (colorArg.startsWith("\"#") && colorArg.endsWith("\"")) {
                                    val hex = colorArg.substring(2, colorArg.length - 1)
                                    "0xFF$hex"
                                } else {
                                    "0xFFCCCCCC" // Default fallback
                                }

                                modifiers.add(".background(Color($hexColor))")
                                processedStatements.add(statement)
                            }
                        }
                    }
                    "orientation" -> {
                        // Handle orientation = LinearLayout.HORIZONTAL
                        if (right is KtDotQualifiedExpression) {
                            val receiver = right.receiverExpression.text
                            val selector = right.selectorExpression?.text ?: ""

                            if (receiver == "LinearLayout" && selector == "HORIZONTAL") {
                                isHorizontal = true
                                processedStatements.add(statement)
                            }
                        }
                    }
                }
            }
        }

        // If no modifiers were found, return an empty modifier
        val modifierString = if (modifiers.isEmpty()) {
            "Modifier"
        } else {
            "Modifier" + modifiers.joinToString("")
        }

        return PropertyParseResult(modifierString, processedStatements, isHorizontal)
    }
    }
