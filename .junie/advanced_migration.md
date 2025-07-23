Project Goal:
Implement the final and most advanced feature: automatically converting custom, reusable Anko functions (e.g., themedHeader) into modern, standalone @Composable functions.

Context:
This builds on all previous steps. The converter can handle standard Anko layouts, modifiers, and various UI elements. This step involves analyzing the user's own functions, transforming them, and then updating the call sites.

Testing Requirements:
Framework: Continue to use JUnit 5 for all tests.

Comprehensive Test Case: Create a new test function, testConvertsCustomAnkoFunctionAndCallSite.

Assertion: This test must process an input string containing both the definition of a custom Anko function and a call to it within a larger layout. The test must assert that the entire transformed output string is correct, which includes the newly generated @Composable function and the updated layout that calls it.

Architectural Approach (A Multi-Pass Strategy):
To solve this, the converter must operate in multiple passes over the code. You must refactor the main convertAnkoToCompose function to orchestrate this process.

Pass 1: Discover and Transform Custom Function Definitions

Before running the main AnkoPsiVisitor, perform a preliminary traversal of the entire PSI file to find all KtNamedFunctions that are custom Anko components. You can identify these by looking for receiver types like _LinearLayout, _RelativeLayout, etc., or the @AnkoViewDslMarker annotation.

For each custom function found, recursively run the core conversion logic on its body.

Generate and store the complete text of a new @Composable function, preserving the original function name, parameters, and visibility, but with the transformed body.

After processing, delete the original Anko function definition from the PSI tree.

Pass 2: Dynamically Update the Transformer Registry

After Pass 1, you will have a list of custom function names that have been converted (e.g., "themedHeader", "statBlock").

For each of these names, dynamically register a new, simple CustomFunctionTransformer in the TransformerRegistry. This transformer's job is simply to ensure the function call is preserved as is, since the name doesn't change.

Pass 3: Run the Main Conversion

Now, run the main AnkoPsiVisitor on the PSI tree as usual. When it encounters a call to themedHeader, it will find the CustomFunctionTransformer registered in Pass 2 and process it correctly.

Pass 4: Final Code Assembly

After the main visitor has finished modifying the tree, retrieve the transformed main layout's text.

Construct the final output string by prepending all the generated @Composable functions (from Pass 1) to the transformed main layout code.

Concrete Example & Test Case:
Anko Input (Entire File Content):

Kotlin

// Custom Anko function definition
private fun @AnkoViewDslMarker _LinearLayout.themedHeader(title: String) {
textView(title) {
textSize = 24f
}.lparams(width = matchParent)
}

// Main layout that calls the custom function
verticalLayout {
padding = dip(16)

    themedHeader("My Profile") // The call site

    button("A Button")
}
Expected Compose Output (Entire File Content):

Kotlin

// The newly generated @Composable function
@Composable
private fun ThemedHeader(title: String) {
Text(
title,
fontSize = 24.sp,
modifier = Modifier.fillMaxWidth()
)
}

// The transformed main layout
Column(
modifier = Modifier.padding(16.dp)
) {
ThemedHeader("My Profile") // The call site is preserved

    Button("A Button")
}
Final Deliverables:
A refactored convertAnkoToCompose function that orchestrates the multi-pass strategy.

Updated AnkoToComposeConverterTest.kt with the new, comprehensive test for custom functions.

A new CustomFunctionTransformer class (optional, could be a simple lambda in the registry).