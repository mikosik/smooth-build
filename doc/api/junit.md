## String junit<>(File tests, [File] deps, String include)

Executes junit tests and fails with error if any of the tests fail.

| Name    | Type   | Default        | Description                                                                                                                              |
|---------|--------|----------------|------------------------------------------------------------------------------------------------------------------------------------------|
| tests   | File   |                | Jar file containing test classes.                                                                                                        |
| deps    | [File] |                | Array of jars containing junit implementation and other dependencies needed to run tests.                                                |
| include | String | "**Test.class" | Pattern matching java class files from which tests should be run. Works the same way as 'include' param in [filter](filter.md) function. |

Returns __String__ equal to 'SUCCESS'.

### examples

Takes all files from "src" directory, compiles them and executes all tests
among them.

```
File testJar = files("src/test") > javac() > jar() > File("test.jar");
String test = testJar > junit([file("junit.jar")]);
```

Takes all files from "src" directory, 
compiles them and executes only tests from `org.smoothbuild` package and 
its subpackages recursively which class name ends with `Test`.

```
File testJar = files("src/test") > javac() > jar() > File("test.jar");
String test = testJar > junit([file("junit.jar")], "org/smoothbuild/**Test.class");
```
