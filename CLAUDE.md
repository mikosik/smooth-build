# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

smooth-build is a build tool featuring a statically typed, purely
functional language with Hindley-Milner type inference, lazy
evaluation, content-addressable caching, and automatic parallel
execution. Written in Java 25.

## Build Commands

```bash
./gradlew build                    # Full build (compile + test + checks)
./gradlew test                     # Run all tests
./gradlew :src:virtual-machine:test  # Run tests for one module
./gradlew :src:virtual-machine:test --tests "org.smoothbuild.virtualmachine.SomeTest"  # Single test class
./gradlew spotlessApply            # Auto-format code
./gradlew :src:cli:fatJar          # Build distributable fat JAR
```

## Code Quality Tools

- **Spotless**: Auto-formatter using Palantir Java Format (Google
  style). Run `./gradlew spotlessApply` before committing.
- **ErrorProne + NullAway**: Static analysis enforced at compile time.
  All code under `org.smoothbuild` must have JSpecify nullness
  annotations.

## Module Architecture

Multi-module Gradle project under `src/`. The compilation pipeline
flows: parsing -> frontend -> backend -> VM evaluation.

| Module | Purpose |
|---|---|
| `common` | Shared utilities: custom collections, filesystem, hashing, scheduling |
| `antlr-smooth` | ANTLR4 grammar and generated parser |
| `compiler-frontend` | Semantic analysis, type checking/inference, produces **S**-prefixed IR |
| `compiler-backend` | Compiles S-prefixed IR to **B**-prefixed bytecode |
| `virtual-machine` | Bytecode VM with parallel evaluation and hash-based caching |
| `evaluator` | Top-level orchestrator combining compiler + VM |
| `cli` | PicoCLI-based command-line interface |
| `standard-library` | `std_lib.smooth` + native Java implementations in `org.smoothbuild.stdlib` |
| `testing` | Shared test fixtures |
| `system-test` | Integration tests |

Gradle module paths use `:src:<module>:` prefix
(e.g., `:src:compiler-frontend:test`).

## Naming Conventions

- **S-prefix**: Frontend/semantic IR classes
  (`SExpr`, `SCall`, `SType`) in
  `org.smoothbuild.compilerfrontend.lang.*`
- **B-prefix**: Bytecode/VM classes
  (`BExpr`, `BValue`, `BCall`, `BOperation`) in
  `org.smoothbuild.virtualmachine.bytecode.*`
- **P-prefix**: Parse tree classes from ANTLR

## Key Design Patterns

- **Custom functional collections**: Use `org.smoothbuild.common.collect`
  (`List`, `Map`, `Set`, `Maybe`) instead of JDK collections.
- **Immutability**: All values are immutable. Records are used for
  data classes.
- **Content-addressable storage**: Expressions are indexed by
  SHA-256 hash via `BExprDb`/`HashedDb` for caching.
- **Dependency injection**: Dagger is used throughout for wiring.

## Code Style Rules

- Never add Javadocs or comments unless explicitly asked. Update
  existing ones if modifying annotated code.
- JUnit test methods use `snake_case` naming.
- Markdown files: max 80 characters per line.
- Format with Spotless before committing.
- Maintain `CHANGELOG.md` for user-visible changes.
