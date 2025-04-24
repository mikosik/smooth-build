# Smooth-Build Developer Guidelines

## Project Overview
Smooth-build is a build tool featuring a strongly typed functional language
with lazy evaluation, fine-grained caching, and automatic parallel execution.

## Project Structure
- **Multi-module Gradle project** with modules organized under `src/`:
  - `common` - Shared utilities and core components
  - `antlr-smooth` - ANTLR grammar for the language
  - `compiler-frontend` - Front-end of the compiler
  - `virtual-machine` - Virtual machine implementation
  - `compiler-backend` - Back-end of the compiler
  - `evaluator` - Expression evaluation
  - `cli` - Command-line interface
  - `standard-library` - Standard library for smooth language
  - `system-test` - System-level tests
  - `distribution` - Distribution packaging
  - `common-testing` - Shared testing utilities

## Tech Stack
- **Java 24** - Primary programming language
- **Gradle** - Build system
- **ANTLR4** - Parser generator for the language
- **Guice** - Dependency injection
- **Guava** - Utility libraries
- **JUnit 5** - Testing framework
- **Mockito & Truth** - Testing utilities

## Building and Testing

### Building the Project
```bash
./gradlew build
```

### Running Tests
```bash
# Run all tests
./gradlew test

# Run tests for a specific module
./gradlew :src:virtual-machine:test

# Run a specific test class
./gradlew :src:virtual-machine:test --tests "org.smoothbuild.virtualmachine.SomeTest"
```

## Each module organization

### Source Code Organization
Each module follows the standard Maven/Gradle structure:
- `src/main/java` - Production code
- `src/test/java` - Test code
- `src/testFixtures/java` - Shared test utilities

### Test Organization
- Unit tests are located in the `test` directory of each module
- System tests are in the `system-test` module
- Test fixtures are shared via the `testFixtures` source set

## Documentation
- `README.md` - Project overview
- `doc/tutorial.md` - User tutorial
- `doc/api.md` - API documentation
- `doc/install.md` - Installation instructions
- `doc/glossary.md` - Terminology definitions
- `doc/dev/` - Developer-specific documentation

## Best Practices
1. **Follow the functional paradigm**
   The project is built around functional programming principles.
   When possible use classes from `org.smoothbuild.common.collect` instead of JDK collections.
2. **Write tests**
   Maintain high test coverage.
   Name junit test methods using snake_case.
3. **Use dependency injection**
   Follow the Guice patterns established in the codebase
4. **Documentation**
   Never add Javadocs nor comments unless explicitly asked.
   Update existing Javadocs or comments when they exist for code you modified.
   Make code readable enough so it speaks for itself.
   Update documentation in *.md files when needed.
5. **Format your code**
   Run Spotless before committing
6. **Code style**
   Format code using spotless plugin.
   In *.md files line length must be limited to 80 characters.
7. **Changelog**
   Maintain CHANGELOG.md in the project root that holds user visible changes.
