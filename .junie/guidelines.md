Project Goal:
Create a proof-of-concept (PoC) command-line tool in Kotlin that translates a simple Anko DSL code snippet into its Jetpack Compose equivalent.

Core Requirements:
Technology: The translation must be done by parsing the input Kotlin code into an Abstract Syntax Tree (AST). Use the kotlin-compiler-embeddable library for this purpose. Do not use simple text replacement or regular expressions.

Functionality: The tool will be a single Kotlin file with a main function. It will take a hardcoded Anko code string as input, transform it, and print the resulting Jetpack Compose code to the console.

Scope: The PoC only needs to support the following Anko-to-Compose transformations:

verticalLayout { ... } should become Column { ... }

textView("Your Text") should become Text("Your Text")

button("Click Me") { onClick { ... } } should become Button(onClick = { ... }) { Text("Click Me") }

The code inside the onClick lambda should be preserved exactly as is.

Concrete Example:
This is the most critical part of the prompt. Provide a clear "before and after."

Given this exact input string:

Kotlin

verticalLayout {
textView("Hello, Anko!")

    button("Click Here") {
        onClick {
            println("Button was clicked!")
        }
    }
}
The program must produce this exact output string:

Kotlin

Column {
Text("Hello, Anko!")

    Button(onClick = {
        println("Button was clicked!")
    }) {
        Text("Click Here")
    }
}
Note: Necessary imports for Compose (androidx.compose.foundation.layout.Column, androidx.compose.material.Button, androidx.compose.material.Text) should be added to the output.

Deliverables:
A build.gradle.kts file configured with the kotlin-compiler-embeddable dependency.
A single Kotlin source file (Main.kt) containing:
A main function that executes the conversion using the example input above.
The core transformation logic that uses the AST parser to perform the required replacements.
Clear comments explaining the process of traversing the AST and transforming the nodes.