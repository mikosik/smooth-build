
## javac

Compiles array of java files.

| Name    | Type     | Default | Description                                                                                                           |
|---------|----------|---------|-----------------------------------------------------------------------------------------------------------------------|
| srcs    | [File]   |         | Array of java files to be compiled.                                                                                   |
| libs    | [Blob]   | []      | Array of jars containing libraries (*.class files) that java source code files passed via 'srcs' parameter depend on. |
| source  | String   | "1.8"   | Version of Java language that given 'srcs' files should be compatible with.                                           |
| target  | String   | "1.8"   | Version of JVM for which *.class files should be generated.                                                           |
| options | [String] | []      | Array of command line options.                                                                                        |

Returns __[File]__ with compiled java classes (*.class files).

### examples

Takes all files from "src" directory and compiles them.

```
[File] classFiles = projectFiles("src") | javac ;
```
