Project Goal:
Expand the converter's capabilities by adding support for more Anko UI elements: linearLayout, imageView, and a view used as a divider. Crucially, for every new piece of functionality, a corresponding unit test must be written to ensure correctness.

Context:
This task builds on the previous steps. The converter can already handle Column, Text, Button, and a unified Modifier system for lparams and body properties.

Testing Requirements (Top Priority):
Framework: All new functionality must be accompanied by unit tests using the JUnit 5 framework.

Test File: Create a new test file located at src/test/kotlin/com/sparkedember/ankotocompose/AnkoToComposeConverterTest.kt.

Test Structure: Each new feature must have its own test function annotated with @Test. Name the functions descriptively (e.g., testConvertsHorizontalLinearLayoutToRow).

Assertions: Each test should call the main convertAnkoToCompose function with a specific Anko input string and use assertEquals to verify that the output string exactly matches the expected Compose code.

Specific Implementation Requirements:
You will create and register three new Transformer classes.

1. Create LinearLayoutTransformer:

It must be registered for the "linearLayout" function name.

Conditional Logic: This transformer's primary job is to inspect the orientation property inside the Anko lambda.

If orientation = LinearLayout.HORIZONTAL is found, it must generate a Row composable.

If the orientation is vertical or not specified, it must generate a Column composable.

It must fully support the existing Modifier parsing from Steps 1 and 2.

2. Create ImageViewTransformer:

It must be registered for the "imageView" function name.

It should convert an Anko imageView into a Compose Image.

It needs to handle the imageResource property, converting imageResource = R.drawable.foo to painter = painterResource(R.drawable.foo).

3. Create ViewDividerTransformer:

It must be registered for the "view" function name.

Conditional Logic: It should only convert the view to a Compose Divider if it's being used as a simple horizontal line. For this proof-of-concept, you can assume this is true if its lparams contains a height of dip(1).

Any other use of view can be ignored for now.

Concrete Examples & Test Cases:
Use these examples to guide both your implementation and the tests you write

1. For LinearLayoutTransformer:
### Anko Input:
```kotlin
linearLayout {
    orientation = LinearLayout.HORIZONTAL
    padding = dip(8)
    textView("Left")
    textView("Right")
}
```

### Expected Compose Output:
```kotlin
Row(
    modifier = Modifier.padding(8.dp)
) {
    Text("Left")
    Text("Right")
}
```

2. For ImageViewTransformer:

Anko Input:

```kotlin
imageView {
    imageResource = android.R.drawable.sym_def_app_icon
}.lparams(width = dip(80))
```
Expected Compose Output:

```kotlin
Image(
    painter = painterResource(android.R.drawable.sym_def_app_icon),
    contentDescription = null,
    modifier = Modifier.width(80.dp)
)
```

3. For ViewDividerTransformer:

Anko Input:
```kotlin
view {
    backgroundColor = Color.LTGRAY
}.lparams(width = matchParent, height = dip(1))
```
Expected Compose Output:

```kotlin
Divider(
    color = Color.LightGray,
    modifier = Modifier
    .fillMaxWidth()
    .height(1.dp)
)
```
Final Deliverables:
Modified main Kotlin file with the three new transformers created and registered.

A new AnkoToComposeConverterTest.kt file containing at least three new @Test functions proving the functionality for Row, Image, and Divider works as specified.