The Blockers 🚧
The Visitor is Unused: You've defined an AnkoPsiVisitor class, but the main convertAnkoToCompose function doesn't actually use it. It uses its own helper functions (findAnkoExpressions, transformAnkoExpressions), which don't support the recursive traversal needed for nested layouts.

Still Generating Strings: The Transformer interface returns a String. This is the main blocker. To handle nesting, you need to transform a KtElement (an AST node) into a new KtElement and replace it in the tree, not just generate a string of text.

Fallback Logic: The handleDirectStringMatching function is still acting as a crutch. A robust AST-based system won't need it.

The Path to a 'Fully Fleshed Out' Converter
To handle nested views, you need to commit fully to AST manipulation. This means making two fundamental changes to your architecture.

Step 1: Make the Visitor the Core of the Logic
Your AnkoPsiVisitor should be the engine. The main function's job is just to set it up and run it. The visitor itself should perform the replacements.

First, delete the findAnkoExpressions and transformAnkoExpressions functions. Then, modify your visitor to perform the replacement directly.

Kotlin

class AnkoPsiVisitor(
private val psiFactory: KtPsiFactory,
private val transformerRegistry: TransformerRegistry
) : KtTreeVisitorVoid() {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression) // Visit children first

        val functionName = expression.calleeExpression?.text
        val transformer = transformerRegistry.getTransformer(functionName)

        if (transformer != null) {
            // The magic happens here!
            val newElement = transformer.transform(expression, psiFactory)
            expression.replace(newElement)
        }
    }
}
Step 2: Embrace KtPsiFactory
This is the most important change. Your transformers must create new AST nodes, not strings.

1. Change the Transformer interface:

Kotlin

// BEFORE
interface Transformer {
fun transform(expression: KtCallExpression): String
}

// AFTER
interface Transformer {
fun transform(expression: KtCallExpression, factory: KtPsiFactory): KtElement
}
2. Update your transformers to use the factory:

Kotlin

// BEFORE (TextViewTransformer)
class TextViewTransformer : Transformer {
override fun transform(expression: KtCallExpression): String {
val textArg = expression.valueArguments.first().getArgumentExpression()?.text ?: "\"\""
return "Text($textArg)\n"
}
}


// AFTER (TextViewTransformer)
class TextViewTransformer : Transformer {
override fun transform(expression: KtCallExpression, factory: KtPsiFactory): KtElement {
val textArg = expression.valueArguments.firstOrNull()?.getArgumentExpression()?.text ?: "\"\""
// Create a real PSI element for the 'Text("...")' call
return factory.createExpression("Text($textArg)")
}
}
3. Update the VerticalLayoutTransformer to enable nesting:
   This is where it all comes together. The layout transformer doesn't generate strings for its children. It just lets the visitor recursively handle them first. Because the visitor replaces the child nodes (textView, button) in the tree, the VerticalLayoutTransformer can then simply transform the parent layout frame.

Kotlin

class VerticalLayoutTransformer : Transformer {
override fun transform(expression: KtCallExpression, factory: KtPsiFactory): KtElement {
// The children have already been transformed by the visitor!
// We just need to change the parent from 'verticalLayout' to 'Column'.
val body = expression.lambdaArguments.firstOrNull()?.getLambdaExpression()?.text ?: "{}"
return factory.createExpression("Column $body")
}
}
By making these changes, your tool will stop being a text generator and become a true code transformation engine. It will naturally handle deeply nested layouts because the visitor will recursively transform the tree from the inside out.