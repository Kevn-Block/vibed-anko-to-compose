# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

AnkoToCompose is a Kotlin tool that converts Android Anko DSL code to Jetpack Compose code using AST (Abstract Syntax Tree) transformation. The project consists of two modules:

- **app**: Android application module that demonstrates the converter
- **converter**: JVM module containing the core conversion logic

## Build Commands

- **Build project**: `./gradlew build`
- **Run converter**: `./gradlew :converter:run`
- **Run tests**: `./gradlew test`
- **Run converter tests**: `./gradlew :converter:test`
- **Run Android app**: `./gradlew :app:assembleDebug` or use Android Studio

## Architecture

### Core Components

1. **Main.kt** (converter): Entry point with `convertAnkoToCompose()` function that orchestrates the conversion process using a multi-pass strategy
2. **ModifierParser.kt**: Utility for parsing Anko layout parameters (`lparams`) and property assignments, converting them to Compose Modifier chains
3. **Transformers**: Interface-based system for converting specific Anko elements:
   - `VerticalLayoutTransformer`: verticalLayout → Column
   - `LinearLayoutTransformer`: linearLayout → Column/Row (based on orientation)
   - `FrameLayoutTransformer`: frameLayout → Box (supports properties, lparams, and multiple children)
   - `CoordinatorLayoutTransformer`: coordinatorLayout → Scaffold (maps AppBarLayout to TopAppBar, FloatingActionButton to FAB slot, and content with proper padding)
   - `TextViewTransformer`: textView → Text
   - `ButtonTransformer`: button → Button
   - `ImageViewTransformer`: imageView → Image
   - `ViewDividerTransformer`: view → Divider (for divider lines)
   - `CustomFunctionTransformer`: Handles user-defined Anko functions

### Conversion Process

The converter uses a 4-pass strategy:
1. **Pass 1**: Discover custom Anko functions using `CustomFunctionDiscoveryVisitor`
2. **Pass 2**: Register transformers for discovered custom functions
3. **Pass 3**: Transform the main layout code using `AnkoPsiVisitor`
4. **Pass 4**: Assemble final code with imports, custom @Composable functions, and transformed layout

### Key Technical Details

- Uses Kotlin Compiler's PSI (Program Structure Interface) for AST manipulation
- Handles property assignments (backgroundColor, padding, orientation) within layout lambdas
- Supports `lparams` conversion to Compose Modifier chains
- Automatically generates necessary Compose imports
- Converts custom Anko functions to @Composable functions with capitalized names

### CoordinatorLayout Conversion

The `CoordinatorLayoutTransformer` provides sophisticated mapping from Anko's CoordinatorLayout to Compose's Scaffold:

- **AppBarLayout + Toolbar** → **TopAppBar** in Scaffold's `topBar` slot
- **FloatingActionButton** → **FloatingActionButton** in Scaffold's `floatingActionButton` slot  
- **ScrollView/RecyclerView/Content** → Main content in Scaffold's content lambda with proper `paddingValues`
- **Coordinated Scrolling** → Automatically handled by Scaffold's built-in behavior
- **Property Support** → Maintains backgroundColor, padding, and other properties via Modifier chains
- **lparams Support** → Converts layout parameters to appropriate Compose modifiers

This provides a direct migration path from Android's complex CoordinatorLayout system to Compose's declarative Scaffold structure.

### Testing

- Unit tests in `converter/src/test/kotlin/com/sparkedember/ankotocompose/`
- Tests cover individual transformers and end-to-end conversion scenarios
- Uses JUnit for test framework

### Dependencies

- Kotlin Compiler Embeddable for AST processing
- Android Compose BOM for UI components
- Standard Android/Kotlin libraries