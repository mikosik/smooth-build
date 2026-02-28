# smooth-build

smooth-build is a build tool where the build script is a statically
typed, purely functional program. Independent values evaluate in
parallel automatically, and every result is cached by content hash,
so no work is ever repeated.

## Key features

- Statically typed, purely functional language with type inference
- Pipe syntax for readable build pipelines
- Content-addressable caching — never recomputes already-seen results
- Automatic parallel execution — no configuration needed
- Lazy evaluation — only what is requested gets computed

## Getting started

1. [Install smooth](doc/install.md)
2. Write a `build.smooth` file in your project root
3. Run `smooth build <value>` where `<value>` is replace by some value defined in build script

## Quick example

The simplest build — compile and jar a Java project in one line:

```
release = files("src") > javac() > jar();
```

Pipe syntax is syntactic sugar. With explicit types, the steps
become clear:

```
String path = "src";
[File] sources = files(path);
[File] classes = javac(sources);
Blob release = jar(classes);
```

Two modules built in parallel with no extra configuration:

```
Blob javaModule(String path) = files(path) > javac() > jar();
commonsJar = javaModule("src/commons");
pluginsJar = javaModule("src/plugins");
```

Running `smooth build commonsJar pluginsJar` evaluates both values
concurrently because they share no data dependencies.

## Language at a glance

### Base types

`String`, `Blob`, `Int`, and `Bool` cover the primitives:

```
String greeting = "Hello, world!";
Blob magic = 0xCAFEBABE;
Int answer = 42;
Bool done = true;
```

### Arrays

Array type is written `[ElementType]`. Access elements at zero-based index with `elem`:

```
[Int] primes = [2, 3, 5, 7, 11];
Int fourth = elem(primes, 3);
```

### Structures

Named fields with an auto-generated constructor:

```
Person {
  String name,
  Int age,
}
Person john = Person("John", 30);
String name = john.name;
```

### Functions

Functions are first-class citizens. Parameters can have default values.

```
Blob subprojectJar(String path, String jdkVer = "17") = files(path) > javac(target=jdkVer) > jar();
commonsJar = subprojectJar("src/commons");
pluginsJar = subprojectJar("src/plugins", "21");
```

### Lambdas

Lambda use `(Int i) -> i` syntax. Parameter types are inferred when not present.
Parentheses are optional when there is only one parameter.

```
[String] names = map(persons, person -> person.name);
```

See the [tutorial](doc/tutorial.md) for full language coverage.

## Caching

Every operation is keyed by its bytecode instruction and the content
hash of its arguments. Adding whitespace to a `.java` file triggers
recompilation, but if the resulting `.class` files are identical the
`jar` step is served from cache immediately. See the
[caching section](doc/tutorial.md#caching) of the tutorial.

## Standard library

The [standard library](doc/api.md) provides Java toolchain functions
(`javac`, `jar`, `junit`, `mavenArtifact`), file I/O (`file`,
`files`, `filterFiles`), array operations (`map`, `filter`, `fold`,
`elem`), and compression utilities (`compressZip`, `compressUnzip`).

See the [tutorial](doc/tutorial.md) for a full walkthrough.

[![CB](https://github.com/mikosik/smooth-build/actions/workflows/gradle.yml/badge.svg)](https://github.com/mikosik/smooth-build/actions/workflows/gradle.yml)
