

## smooth-build tutorial

Smooth build is a build tool that uses statically typed 
functional language to specify the build process.
All values are immutable 
and all build process steps are specified explicitly.
These two features make reasoning about a build process trivial
and discovering how things work under the hood possible.

This tutorial focuses mostly on smooth language,
so you may get an impression
that smooth build favors configuration over convention.
This is far from the truth.
Once all features are implemented, it will be possible to declare
separate smooth modules that can encapsulate functions and structures
that provide default values for most common settings and
 the possibility to alter them according to specific project requirements.

### Simplest example

Let's start with the simplest (almost trivial) build - 
compiling and jarring java code using pipe syntax.
```
release = files("src") > javac() > jar();
```

While pipe syntax is more readable, 
it is just a syntactic sugar for normal function calls.
In pipe syntax output from one pipe element
becomes implicit first argument to the next function call.
Let's rewrite the code above to more familiar syntax with nested function calls:

```
release = jar(javac(files("src")));
```

That code calculates `release` value by:
 - taking files from `src` directory (using [files](api/files.md) function)
 - compiling them with java compiler (using [javac](api/javac.md) function)
 - compressing into jar (using [jar](api/jar.md) function)

All functions used in that tutorial come from [smooth standard library](api.md).

Before we can build the jar file specified above, we first need:
 - [download and install smooth](install.md)
 - place that code in `build.smooth` file located in your project root directory
 - create `src` directory (in project root directory) with some java files

Once everything is in place you can run `smooth build release` 
(or `smooth.bat build release` on Windows). 
This command starts build process that builds (evaluates) `release` value
and prints (among others) the following summary:

````
:cli:build:saveArtifacts
  [INFO] release -> '.smooth/artifacts/release'
````

That output informs that `release` value has been evaluated 
and stored as `.smooth/artifacts/release` in your project directory.
Each time you execute build, it first deletes all files in `.smooth/artifacts`
so after build completes that directory contains only freshly built artifacts.



### Values and Types

Let's rewrite our initial example so the result of each expression
is assigned to a separate named value.
This helps us understand what values are passed as arguments
and what their types are.

```
String path = "src";
[File] sources = files(path);
[File] classes = javac(sources);
Blob release = jar(classes);
```

Each value, no matter whether literal (like `"src"`) 
or named value (like `sources`) is immutable.
Its name must start with a small letter and by convention smooth uses camelCase.

Type names (`String`, `Blob`, etc.) start with capital letters and by convention use UpperCamelCase.
Types inside brackets `[]` denotes arrays - more on that later.
In the example above we specified the type of each value explicitly,
however, we can omit type declarations, and they would be inferred by compiler.

```
path = "src";
sources = files(path);
classes = javac(sources);
release = jar(classes);
```

For educational reasons we explicitly specify all types in our examples from now on.

Smooth language has four base types:
 - `String` sequence of characters.
 - `Blob` sequence of bytes, usually used to represent content of a file
 - `Int` unbounded signed integers
 - `Bool` boolean type with two allowed values `true` and `false`

Value of each type can be defined inline using its literal.
 - `String` - enclose string using double quotes, for example `"Hello, world!"`,
    special characters in string literals are escaped using backslash, for example
    `"line1\nline2"`.
 - `Blob` - prefix hexadecimal digits with `0x`, for example `0xCAFEBABE`
 - `Int` - provide all digits optionally prefixed with `-`, for example `-378`
 - `Bool` - use one of predefined values - `true` or `false`

Apart from base types, smooth contains also composite types -
arrays, structures and functions.
They work the same way as in other languages.
Let's explore each one in more detail.


#### Arrays

Array is a sequence of elements of the same type.
Array type is referenced by enclosing its element type inside brackets `[]`.
Array literal is declared enclosing comma separated elements (expressions) inside brackets `[]`.
Elements of an array are accessed using [elem](api/elem.md) function
which uses an index that starts at zero.
Let's see everything we just explained in the code below:

```
[Int] primeNumbers = [2, 3, 5, 7, 11];
Int fourthPrimeNumber = elem(primeNumbers, 3);
```


#### Tuples

Tuple is an ordered collection of elements that can have different types.
Tuple type is referenced by enclosing its element types inside curly braces `{}`.
Tuple literal is declared by enclosing comma separated expressions inside curly braces `{}`.
Here's an example of a tuple with three elements of different types:

```
{String, Int, Bool} personData = {"John", 25, true};
```

Elements of a tuple can be accessed using dot notation followed by the one-based index of the element:

```
String name = personData.1;    // "John"
Int age = personData.2;        // 25
Bool isActive = personData.3;  // true
```

Tuples are useful when you need to group multiple values together without creating
a named structure. They can be used as function return values or as temporary
containers for related data.


#### Structures

Structure is a composite of named values known as its fields with potentially different types.
Structures and their values are immutable (like all smooth values).
Structure type is defined by providing UpperCamelCase name
and parenthesized comma-separated list of fields (named values).
Structure type is referenced by providing its name.
Structure field value is accessed using `.` operator.
Each Structure type has an auto-generated constructor, 
which is a function with the same name as the structure 
and parameter types and names matching structure fields.
Let's see all that in action.

```
Person {
  String firstName,
  String lastName,
}
Dog {
  String name,
  Person owner,
}
Dog rex = Dog("Rex", Person("John", "Doe"));
String ownerName = rex.owner.lastName;
```

Most common structure is `File` that is defined in standard library:

```
File {
  Blob content,
  String path,
}
```

It is just a file path and file content.
It is used by many functions from the standard library, the most popular are:
 - [file](api/file.md) - reads single file from the project at a given path
 - [files](api/files.md) - reads all files from given directory inside project
 - [filterFiles](api/filterFiles.md) - filters array of files according to glob pattern
 - [jar](api/jar.md) - jars array of files
 - [compressZip](api/zip.md) - zips array of files


#### Functions

Function is a self-contained block
that takes parameters and returns a single result.
It can be polymorphic - more on that below.
Functions are first-class citizens, and as such they can be:
 - passed to other functions as argument
 - returned from function as its result
 - stored as an array element or structure field
 - assigned to a named value

Let's refactor our initial example that compiles and jars java code,
by extracting handy `subprojectJar` function,
so it can be used for building different modules.

```
Blob subprojectJar(String path) = files(path) > javac() > jar();
commonsJar = subprojectJar("src/commons");
pluginsJar = subprojectJar("src/plugins");
```

Function type is constructed using arrow syntax.
`(Int,Bool)->String` is type of function that takes `Int` and `Bool` and returns `String`.


#### parameter default value

When we define a function, we can provide default values for some parameters.
This way, a call to such a function does not have to provide values for those parameters.
Consider [javac](api/javac.md) function.
So far we always called it with a single argument.
However, if you inspect its documentation, you can see that it has multiple parameters.
Compiler accepts calls to javac with a single argument 
because all parameters except the first have default value.
Let's try passing values for the first and second parameter:

```
[File] sources = files("src");
[File] libs = [file("lib/guava-31.0.1-jre.jar")];
release = javac(sources, libs) > jar();
```

We called [javac](api/javac.md) and provided the array of third-party libraries that our code uses.
Note that currently smooth build does not have a function that could download
a library from maven repositories. This feature is high on the wish list so stay tuned.

We can also simplify that code by using pipes.
Below call to [javac](api/javac.md) passes two arguments -
first is the result of a call to `[files](api/files.md)` piped through `>`,
second is value of `libs` passed directly.

```
[File] libs = [file("lib/guava-31.0.1-jre.jar")];
release = files("src") > javac(libs) > jar();
```

How about calling a function and providing values for the 1st and 4th parameter?
How would the compiler know that the 2nd argument should be assigned to the 4th parameter?
It would not, so we need to write it explicitly using named arguments.
The below code calls [javac](api/javac.md) by providing value for first parameter,
and explicitly assigning value `"17"` to its 4th parameter - `target`.

```
[File] sources = files("src");
release = javac(sources, target="17") > jar();
```


#### Polymorphic functions

Smooth allows declaring polymorphic functions and values via
[parametric polymorphism](https://en.wikipedia.org/wiki/Parametric_polymorphism).
To declare type parameters place them inside angle brackets `<>`.
The below copy of [id](api/id.md) function declaration from [standard library](api.md)
that returns its only parameter.

```
A id<A>(A a) = a;
```

At the invocation site, compiler infers type arguments.
In the example below `id<A>()` function is instantiated to `id<Int>()`. 
```
Int result = id(7);
```


#### Lambdas

Lambda is an unnamed function that can be defined and used inline.
It uses lambda syntax using arrow `->` similarly to other programming languages.
In the following example we use [map](api/map.md) function
that takes an array and function to convert elements of that array.
We call [map](api/map.md) function 
with an array of Persons and lambda for converting elements.

```
Person {
  String name,
  Int age,
}
[Person] persons = [Person("John", 23), Person("Kate", 34)];
[String] names = map(persons, (Person person) -> person.name);
```

As mentioned before, type can be inferred, so above lambda can be shortened to `person -> person.name`.

### Caching

If you run the build command twice for our initial example

```
release = files("src") > javac() > jar();
```

you will notice that the second evaluation completes almost instantly.
That's because the result of each operation has been cached by smooth.
This looks like nothing extraordinary 
as most build tools reuse the result from previous execution.
However, smooth cache system is more fine-grained
as it caches on disk the result of each bytecode operation on given arguments.
When it has to evaluate a given operation again, it simply takes the result from the cache.
We won't delve into details of smooth bytecode and its operations,
for now it is enough to state that every call to native function
(function that is implemented in java not in smooth)
is a bytecode operation.

You can see yourself how cache works by running build for our initial example,
then changing one of java files in `src` directory by adding empty
spaces to the end of some line and then running build again.
When you run the build a second time, you will notice that javac task
is re-executed (as content of *.java files has changed)
but because only formatting of the file changed,
compilation will produce exactly the same *.class files as before.
Smooth won't execute [jar](api/jar.md) function at all
as the result of such execution is already in its cache.
Console output will contain the "cache" word at the end of the line
representing [jar](api/jar.md) function execution.
Note that such optimization is not possible with incremental building
as change to any file at the bottom of the build tree will always
force rebuild of all tasks that depend on it.

Now if you revert changes you introduced to mentioned java file
and run build once again, then the result will be instantaneous.
All function calls (function plus actual argument values) have already been executed
during previous build runs, so they are taken from cache.
The only exception is a call to `files("src")` function which is impure
as it reads files from disk, so its result cannot be cached between builds.

Such a solution is powerful as it gives you access to any build result you have ever executed.
It's enough to checkout relevant code from your repository
and run a build command that will provide results instantly.


### Parallel execution

Smooth build evaluates values in parallel unless they depend on each other.
Let's take a look at one of our previous examples:

```
Blob javaModule(String path) = files(path) > javac() > jar();
commonsJar = javaModule("src/commons");
pluginsJar = javaModule("src/plugins");
```

If we build both jars via `smooth build commonsJar pluginsJar`
then building java jars will be executed in parallel.
We do not need to do anything to make it happen.


### Trailing commas

In every case where comma is used to separate language elements in a sequence,
it may (or may not) be used also after last element.
For example, the following struct definition uses comma after last field

```
Person {
  String firstName,
  String lastName,
}
```

but it is acceptable to skip it without changing semantics

```
Person {
  String firstName,
  String lastName
}
```

This works the same when defining function parameters, arguments in function call,
elements in array literal.
If you format your code so that each such element is on separate line
and use trailing comma, then it's much easier to reorder such elements.
Such a change is also more readable in version control.


### Things not yet implemented

 - modules and imports so functions/values do not pollute global namespace
