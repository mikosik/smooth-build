## Project Overview

smooth-build is a build tool featuring a statically typed, purely
functional language with Hindley-Milner type inference, lazy
evaluation, content-addressable caching, and automatic parallel
execution.

## Build Commands

```bash
./gradlew build                    # Full build (compile + test + checks)
./gradlew test                     # Run all tests
./gradlew :src:virtual-machine:test  # Run tests for one module
./gradlew :src:virtual-machine:test --tests "org.smoothbuild.virtualmachine.SomeTest" # Single test class
./gradlew spotlessApply            # Auto-format code
```

## Module Architecture

Multi-module Gradle project under `src/`. The compilation pipeline
flows: parsing -> frontend -> backend -> VM evaluation.

| Module              | Purpose                                                                |
|---------------------|------------------------------------------------------------------------|
| `common`            | Shared utilities: custom collections, filesystem, hashing, scheduling  |
| `antlr-smooth`      | ANTLR4 grammar and generated parser                                    |
| `compiler-frontend` | Semantic analysis, type checking/inference, produces **S**-prefixed IR |
| `compiler-backend`  | Compiles S-prefixed IR to **B**-prefixed bytecode                      |
| `virtual-machine`   | Bytecode VM with parallel evaluation and hash-based caching            |
| `evaluator`         | Top-level orchestrator combining compiler + VM                         |
| `cli`               | PicoCLI-based command-line interface                                   |
| `standard-library`  | standard library for smooth language                                   |
| `testing`           | Shared test fixtures                                                   |
| `system-test`       | Integration tests run against smooth binary                            |

## Code Style Rules

- Never add Javadocs or comments unless explicitly asked.
  Update existing ones if modifying corresponding code.
- Always run full build when performing final verification of changed code.
