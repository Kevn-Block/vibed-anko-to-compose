### Project Goal:
Enhance the Anko-to-Compose converter to support simple property assignments (e.g., padding, backgroundColor) made directly inside a layout's lambda. These properties must be converted into Modifier chains and combined with any existing modifiers parsed from .lparams().

### Context:
This task builds directly on Step 1. The converter can already parse .lparams(). Now, we need to add a second source for modifiers—the layout's body—and merge them correctly. The changes will primarily affect layout transformers like VerticalLayoutTransformer.

### Specific Requirements:
Enhance Layout Transformers: Update VerticalLayoutTransformer (and any future layout transformers) to inspect the statements inside its KtLambdaExpression body.

Identify Property Assignments: The transformer should look for KtBinaryExpression nodes (assignments using =) within the lambda body.

Extend the ModifierParser: The ModifierParser created in Step 1 should be enhanced. It needs a new method that can take the KtBlockExpression (the body of the lambda) and extract all relevant property assignments, converting them into a Modifier chain. It must handle at least:

padding = dip(...) -> .padding(...dp)

backgroundColor = Color.parseColor("#...") -> .background(Color(0xFF...))

backgroundColor = Color.WHITE -> .background(Color.White)

Handle the orientation Edge Case: The orientation property is special. When orientation = LinearLayout.HORIZONTAL is found, it should not become a modifier. Instead, it should change the root component of the transformed layout from Column to Row.

Combine All Modifiers: The layout transformer must now be able to generate a single, combined Modifier chain from two sources:

The .lparams() call (from Step 1).

The property assignments in the lambda body (this step).

Clean Up the Lambda Body: Once a property assignment like padding = ... has been processed and converted into a modifier, that KtBinaryExpression node must be removed from the lambda body in the final output. The body of the generated Compose function should only contain the child UI components (Text, Button, etc.).

### Concrete Example:
This example is crucial as it tests the merging of .lparams() and body properties.

Given this input Anko code:

```kotlin
verticalLayout {
    // Properties assigned directly in the body
    backgroundColor = Color.parseColor("#EEEEEE")
    padding = dip(16)

    // A child UI element that should remain
    textView("Content here")

}.lparams(width = matchParent) // Modifier source from lparams
```

```kotlin
// Note: verticalLayout became a Column
Column(
    modifier = Modifier
        .fillMaxWidth() // This came from .lparams()
        .background(Color(0xFFEEEEEE)) // This came from the body
        .padding(16.dp) // This also came from the body
) {
    // Note: The processed properties are gone, only the child view remains.
    Text("Content here")
}
```