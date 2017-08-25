## javac

Compiles array of java files.

 * [File] __sources__ (_required_) - Array of java files to be compiled.
 * [Blob] __libs__ - Array of jars containing libraries (*.class files) that
java source code files passed via 'sources' parameter depend on.
 * String __source__ - Version of Java language that given 'sources' files
should be compatible with.
 * String __target__ - Version of JVM for which *.class files should
be genereated.

Returns File[] with compiled java classes (*.class files).

### examples

Takes all files from "src" directory and compiles them.

```
classFiles = files("//src") | javac() ;
```
