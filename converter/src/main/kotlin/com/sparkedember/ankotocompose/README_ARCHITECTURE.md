# AnkoToCompose - Refactored Architecture

## Overview

The AnkoToCompose converter has been refactored from a single monolithic file to a modular, maintainable architecture following best practices.

## Architecture

### Core Interfaces

- **`AnkoConverter`**: Main interface for converting Anko code to Compose
- **`Parser`**: Interface for parsing Anko code into structured representation
- **`LayoutTransformer`**: Interface for transforming layout nodes to Compose code
- **`TransformerRegistry`**: Registry for managing and discovering transformers

### Key Components

#### 1. Parsing Layer
- **`AstParser`**: Uses Kotlin compiler PSI for precise parsing
- **`PatternMatchingParser`**: Simple regex-based parsing for fallback

#### 2. Transformation Layer
- **`ColumnTransformer`**: Handles vertical layouts
- **`TextViewTransformer`**: Converts textView to Text
- **`CoordinatorLayoutWithCollapsingTransformer`**: Handles complex coordinator layouts

#### 3. Configuration
- **`ComposeImports`**: Centralized import constants
- **`AnkoMappings`**: Mapping definitions from Anko to Compose

#### 4. Implementation
- **`HybridAnkoConverter`**: Main converter using hybrid approach (AST + pattern matching)
- **`DefaultTransformerRegistry`**: Default registry implementation

### Benefits of New Architecture

#### 1. **Separation of Concerns**
- Parsing logic separated from transformation logic
- Each transformer handles one specific layout type
- Configuration centralized in dedicated files

#### 2. **Extensibility**
- Easy to add new transformers by implementing `LayoutTransformer`
- Plugin-like architecture with transformer registry
- Multiple parsing strategies can coexist

#### 3. **Maintainability**
- Small, focused classes instead of one giant file
- Clear interfaces make testing easier
- Proper error handling with specific exception types

#### 4. **Testability**
- Each component can be tested in isolation
- Mock implementations can be easily created
- Clear test organization

#### 5. **Error Handling**
- Structured error types (`ConversionException`, `ParseException`)
- Graceful fallbacks from AST to pattern matching
- Detailed error context and metadata

### Usage

#### Basic Usage
```kotlin
val converter = ConverterFactory.createDefaultConverter()
val result = converter.convert(ankoCode)
println(result.composeCode)
```

#### Custom Configuration
```kotlin
val registry = DefaultTransformerRegistry()
registry.register(MyCustomTransformer())

val converter = HybridAnkoConverter(transformerRegistry = registry)
val result = converter.convert(ankoCode)
```

#### Conversion Result
```kotlin
data class ConversionResult(
    val composeCode: String,
    val imports: Set<String> = emptySet(),
    val warnings: List<String> = emptyList(),
    val metadata: ConversionMetadata = ConversionMetadata()
)
```

### Migration from Old Architecture

The old `convertAnkoToCompose()` function is still available for backward compatibility, but new code should use:

```kotlin
// Old way
val result = convertAnkoToCompose(ankoCode)

// New way  
val converter = ConverterFactory.createDefaultConverter()
val result = converter.convert(ankoCode)
val formattedResult = "${result.imports.joinToString("\n") { "import $it" }}\n\n${result.composeCode}"
```

### Adding New Transformers

1. Implement the `LayoutTransformer` interface:

```kotlin
class MyLayoutTransformer : LayoutTransformer {
    override fun canTransform(node: LayoutNode): Boolean {
        return node.type == "myLayout"
    }
    
    override fun transform(node: LayoutNode, context: TransformationContext): TransformationResult {
        // Implementation here
    }
    
    override val priority: Int = 10
}
```

2. Register it in the factory:

```kotlin
registry.register(MyLayoutTransformer())
```

### Performance Considerations

- AST parsing is tried first for accuracy
- Pattern matching fallback is fast for simple cases
- Transformers are sorted by priority for efficient lookup
- Imports are deduplicated automatically

### Testing

The new architecture includes comprehensive tests:
- Unit tests for individual transformers
- Integration tests for the full conversion pipeline
- Backward compatibility tests
- Performance tests for both parsing strategies

All existing tests continue to pass, ensuring no regressions.