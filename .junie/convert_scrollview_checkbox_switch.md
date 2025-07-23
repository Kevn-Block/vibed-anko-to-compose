Project Goal:
Extend the Anko-to-Compose converter to support three new UI components: scrollView, checkBox, and switch. This involves creating new transformers and writing corresponding unit tests to verify their functionality.

Context:
This task continues the development of the existing converter. You will be adding new classes to the transformers package and new tests to AnkoToComposeConverterTest.kt, leveraging the existing TransformerRegistry and ModifierParser architecture.

Testing Requirements:
Framework: Continue to use JUnit 5.

Test Coverage: Add a new @Test function for each of the three new components (scrollView, checkBox, switch) in the existing AnkoToComposeConverterTest.kt file.

Assertions: Each test must verify that the transformed Compose code string exactly matches the expected output for a given Anko input.

Specific Implementation Requirements:
You will create and register three new Transformer classes.

1. Create ScrollViewTransformer:

Register it for the "scrollView" function name.

It must convert the Anko scrollView into a Compose Column with the Modifier.verticalScroll(rememberScrollState()).

The body of the scrollView should be processed recursively to convert its children.

Note: You will need to ensure the rememberScrollState import is added (androidx.compose.foundation.rememberScrollState).

2. Create CheckBoxTransformer:

Register it for the "checkBox" function name.

An Anko checkBox with text should be converted into a Row containing a Checkbox and a Text label for proper alignment and accessibility.

It must parse the text property for the Text label and the isChecked property for the Checkbox's checked state.

3. Create SwitchTransformer:

Register it for the "switch" function name.

Similar to the checkbox, an Anko switch should be converted into a Row containing a Switch and a Text label.

It must parse the text property for the label and the isChecked property for the Switch's checked state.

Concrete Examples & Test Cases:
Use these examples for your implementation and unit tests.

1. For ScrollView:

Anko Input:

Kotlin

scrollView {
verticalLayout {
padding = dip(16)
textView("Top Content")
textView("Bottom Content")
}
}
Expected Compose Output:

Kotlin

Column(
modifier = Modifier.verticalScroll(rememberScrollState())
) {
Column(
modifier = Modifier.padding(16.dp)
) {
Text("Top Content")
Text("Bottom Content")
}
}
2. For CheckBox:

Anko Input:

Kotlin

checkBox {
text = "Accept Terms"
isChecked = true
}.lparams { topMargin = dip(8) }
Expected Compose Output:

Kotlin

Row(
modifier = Modifier.padding(top = 8.dp),
verticalAlignment = Alignment.CenterVertically
) {
Checkbox(
checked = true,
onCheckedChange = { /* TODO: Handle state change */ }
)
Text(
text = "Accept Terms",
modifier = Modifier.padding(start = 8.dp)
)
}
3. For Switch:

Anko Input:

Kotlin

switch {
text = "Enable Notifications"
isChecked = false
}
Expected Compose Output:

Kotlin

Row(
verticalAlignment = Alignment.CenterVertically
) {
Switch(
checked = false,
onCheckedChange = { /* TODO: Handle state change */ }
)
Text(
text = "Enable Notifications",
modifier = Modifier.padding(start = 8.dp)
)
}