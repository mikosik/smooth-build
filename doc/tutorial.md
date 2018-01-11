### smooth-build tutorial

Smooth uses `build.smooth` file located in project's root directory as
a description of project's build process.
`build.smooth` files are written in (statically typed and functional) smooth language.
One of the simplest non trivial build files is:

```
release_jar = files("//src") | javac | jar;
```

This script defines `release_jar` function that does not have any parameters
and performs following tasks:

 * Invokes `files` function that takes all files (recursively)
 from `src` directory located at project's root. Double slashes `//` denote
 root directory of your project (directory in which given build.smooth file
 is located).
 * Passes them to `javac` function (Java compiler) that compiles those files.
 * Passes compiled files to `jar` function that packs them into jar file.

Note that there's no need to explicitly create temporary directory for *.class
files as they are passed as parameter to `jar` function straight from `javac`
output.

You can invoke execution of `release_jar` function from command line by running:

```
smooth build release_jar
```

Above command executes `release_jar` function and stores its result
as `release.jar` file (`_jar` suffixes are automatically converted to `.jar`)
in `.smooth/artifacts` directory.
If you want to try examples yourself then
[download and install smooth](https://github.com/mikosik/smooth-build/blob/master/doc/install.md)
first.
You can consult
[functions list](https://github.com/mikosik/smooth-build/blob/master/doc/api.md)
for a list of all builtin functions.


### Type system

Each value in smooth language (that is returned by function or
passed as argument to it) is of one of following types:
 * **String** - Sequence of characters
 * **Blob** - Sequence of bytes
 * **File** - Single file - compound object that has a content (Blob) and path associated with it (String).
 * **Nothing** - type that is convertible to any other type but it is not possible to create value of such type.
 * Array ( **[String]**, **[Blob]**, **[File]**, **[Nothing]** )

Smooth language is strongly typed and statically checked.
Value can be assigned to given function parameter if value's type
is equal to or convertible to that parameter's type.
File is convertible to Blob.
Array of some type A ([A]) is convertible to array of type B ([B])
when A is convertible to B.
Nothing is convertible to any type.


### Functions

Function chaining (that is represented by pipe symbol `|`) shown in
initial example is just syntactic sugar for more standard function calls.
Initial example can be refactored into:

```
release_jar = jar(javac(files("//src")));
```

This version is less readable though it is more familiar to people
coming from imperative languages.

Functions declared in `build.smooth` (for example `release_jar`)
can be used the same way as builtin functions (like `javac`).
We can refactor our initial example by splitting it into two functions:

```
classes(String sourcePath) = files(sourcePath) | javac;
release_jar = jar(classes("//src"));
```


### Literals

As you noticed Smooth contains String literals which are specified
by enclosing its charcters inside double quotes as in most languages.
Another literal that you find useful is an array literal that lets you
create vaue of `[String]`, `[Blob]` `[File]` and `[Nothing]` types.
Array literal is comma separated list of expressions enclosed inside brackets `[` `]`.
Empty array literal `[]` has type `[Nothing]` and can is convertible to any other array type.

```
stringValue = "README.md";
stringArrayValue = ["one", "two", "three"];
fileArrayValue = [file(stringValue1), file(stringValue2)];
```


### Function parameters

Most functions can accept more than one argument.
One example is
[javac](https://github.com/mikosik/smooth-build/blob/master/doc/api/javac.md).
Despite that we kept passing only one argument in all above examples,
it didn't cause any error as smooth was capable to infer, to which parameter
this argument should be assigned, by comparing argument's type with function
parameter types.
Parameters left without match are assigned parameter's default value.

However if there's ambiguity (smooth is not able to deduce which arguments
should be assigned to which parameters) then it fails with error.
In such cases disambiguity can be solved by specifying assignment between argument
and parameter explicitly.
In the following example we need to explicitly name `source` parameter, as
without it, smooth wouldnt' be able to guess whether `1.8` String value should
be assigned to `source` or `target` parameter, both of which are of type String.
```
release_jar = files("//src") | javac(source="1.8") | jar;
```

Matching arguments to parameters works according to following algorithm
(Actual implementation doesn't use brute force but it is much easier to write
smooth code when you understand this algorithm from brute force perspective):
 1. All arguments that have explicit assignment specified are assigned to
 those parameters.
 If some parameter is assigned explicitly more than once then algorithm fails.
 2. For arguments without explicit assignment algorithm generates
 all possible set of assignments that met following criteria:
    * each argument is assigned exactly once
    * each parameter without default value is assigned exactly once
    * each parameter with default value is assigned at most once
    * each argument is assigned to parameter which type is convertible from
    that argument type
 3. If there's exactly one set of assignments generated in 2.
 then it is chosen, otherwise algorithm fails with ambiguity.

Note that currently explicit assignment of argument to parameter
cannot be done for value that is passed through a pipe.
This may be a nuisanance as you have to use nested function calls in such cases.


### Caching

If you run build command twice for any example shown above,
you will notice that second run completes almost instantly.
That's because output from `release_jar` function has been cached
by smooth.
This is nothing extraordinary as most build tools reuse result
from previous execution.
However smooth is much smarter.
Its cache system is much fine grained as it keeps results
of each function call it has ever executed.
If it ever has to execute given call (function plus its arguments) again
it just takes result from cache.

You can see how it works by runing build for our initial example,
then changing one of java files in `src` directory by adding empty
line to it and then runing build again.
When you run build second time, you will notice that javac task
is reexecuted (as content of *.java files has changed)
but because only formatting of the file changed,
compilation will produced exactly the same *.class files as before.
smooth won't execute `jar` function at all as it will realize
that it has result of such execution already in its cache.
Note that such optimization is not possible with incremental building
as change to any file at the beginning of the build pipeline will always
force rebuild of all tasks that depend on it.

Now if you revert changes you introduced to mentioned java file
and run build once again then result will be instantaneous as
all function calls have been executed before so their results
are taken from cache.
Such solution gives you access to any build result you have ever executed.
You just need to checkout relevant code version from your repository
and run build command that will provide results instantly.

