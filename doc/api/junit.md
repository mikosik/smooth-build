## junit

Executes junit tests and fails if any of the test fail.

 * Blob[] __libs__ - Array of jar files containing test classes and all
library classes needed for test to run.
All classes which names end with Test will be run.
 * String __include__ - Pattern matching java class files from which
tests should be run. Works the same way as 'include' param
in [filter](filter.md) function.

Returns __String__ equal to 'SUCCESS'.

### examples

Takes all files from "src" directory, compiles them and executes all tests
among them.
Note that in current releases there's no need to pass jar with junit classes.
Junit binaries are embedded inside smooth-all.jar.
This will probably change in future so it would be easy to update junit
 library without updating smooth version.

```
testJar: files("//src") | javac | jar;
test: [ testJar ]  | junit;
```

Takes all files from "src" directory, compiles them and executes ony tests from classes which names end with 'Test'.

```
testJar: files("//src") | javac | jar;
test: [ testJar ]  | junit(include="**/*Test.class");
```
