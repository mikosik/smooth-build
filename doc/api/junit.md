## junit

Executes junit tests and fails if any of the test fail.

 | Name | Type | Default | Description |
 | ---- | ---- | ------- | ----------- |
 | tests | Blob |   | Jar file containing test classes. |
 | deps | [Blob] |   | Array of jars containing junit implementation and other dependencies needed to run tests. |
 | include | String | "**/*Test.class" | Pattern matching java class files from which tests should be run. Works the same way as 'include' param in [filter](filter.md) function. |

Returns __String__ equal to 'SUCCESS'.

### examples

Takes all files from "src" directory, compiles them and executes all tests
among them.
Note that in current releases there's no need to pass jar with junit classes.
Junit binaries are embedded inside smooth-all.jar.
This will probably change in future so it would be easy to update junit
 library without updating smooth version.

```
testJar = files("//src") | javac() | jar();
test = [ testJar ]  | junit();
```

Takes all files from "src" directory, compiles them and executes ony tests from classes which names end with 'Test'.

```
testJar = files("//src") | javac() | jar();
test = [ testJar ]  | junit(include="**/*Test.class");
```
