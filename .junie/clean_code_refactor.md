Refactor the attached Kotlin code for converting Anko to Compose. The current implementation has several major flaws that need to be fixed:

It uses a large if/else if block with hardcoded string checks to cheat and bypass the AST parser for specific examples. This entire block must be removed. All transformations must use the PSI/AST tree.

The code generates the output Compose code using StringBuilder. This is brittle. Refactor it to use a KtPsiFactory to create new KtCallExpression and other PSI elements for the Compose code.

The logic is not modular. You must refactor the design to use a proper Visitor Pattern with a Transformer Registry.

New Architecture Requirements:

Create a primary visitor class (e.g., AnkoPsiVisitor) that extends KtTreeVisitorVoid. Its job is to walk the PSI tree.

When the visitor encounters a KtCallExpression, it should check if the function name (e.g., "textView", "button") exists in a transformer registry (like a Map<String, Transformer>).

Create a Transformer interface with a transform(expression: KtCallExpression, factory: KtPsiFactory): KtElement function.

Create concrete implementations of this interface: TextViewTransformer, ButtonTransformer, and VerticalLayoutTransformer.

Each transformer will contain the logic for converting only its specific Anko element using the KtPsiFactory.

The main AnkoPsiVisitor will call the appropriate transformer and use its result to replace the original Anko node in the PSI tree using the replace() method.

The final output should be the text from the modified PSI tree, which will be well-formatted, valid Compose code.