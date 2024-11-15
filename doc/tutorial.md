

## smooth-build tutorial

Smooth build is a build tool that uses statically typed 
functional language to specify build process.
All values are immutable 
and all build process steps are specified explicitly.
This two features make reasoning about build process trivial
and discovering how things work under the hood possible.

This tutorial focuses mostly on smooth language,
so you may get an impression
that smooth build favours configuration over convention.
This is far from truth.
Once all features are implemented it will be possible to declare
separate smooth modules that can encapsulate functions and structures
that provides default values for most common settings and
possibility to alter them according to specific project requirements.

Smooth build tool is still work in progress,
so it may not match all features of other build tools yet.
If you miss some feature/function you would love to use
just create new github issue.


### Simplest example

Let's start with the simplest (almost trivial) build - 
compiling and jarring java code using pipe syntax.
```
release = files("src") > javac() > jar();
```

While pipe syntax is more readable 
it is just a syntactic sugar for normal function calls.
In pipe syntax output from one pipe element
becomes implicit first argument to next function call.
Let's rewrite code above to more familiar syntax with nested function calls:

```
release = jar(javac(files("src")));
```

That code calculates `release` value by:
 - taking files from `src` directory (using [files](api/files.md) function)
 - compiling them with java compiler (using [javac](api/javac.md) function)
 - compressing into jar (using [jar](api/jar.md) function)

All functions used in that tutorial come from [smooth standard library](api.md).

Before we can build jar file specified above, we need first:
 - [download and install smooth](install.md)
 - place that code in `build.smooth` file located in your project root directory
 - create `src` directory (in project root directory) with some java files

Once everything is in place you can run `smooth build release` 
(or `smooth.bat build release` on Windows). 
This command starts build process that builds (evaluates) `release` value
and prints (among others) following summary:

````
Saving artifact(s)
  release -> '.smooth/artifacts/release'
````

That output informs that `release` value has been evaluated 
and stored as `.smooth/artifacts/release` in your project directory.
Each time you execute build, it first deletes all files in `.smooth/artifacts`
so after build completes that directory contains only freshly built artifacts.



### Values and Types

Let's rewrite our initial example so result of each expression
is assigned to separate named value.
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
Value names are written with lowerCamelCase.

Types (`String`, `Blob`, etc) are always written with UpperCamelCase.
Types inside brackets `[]` denotes arrays - more on that later.
In example above we specified type of each value explicitly
however we can omit type declarations, and they would be inferred by compiler.

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
 - `String` - enclose string using double quotes, for example `"Hello, world!"`
 - `Blob` - prefix hexadecimal digits with `0x`, for example `0xCAFEBABE`
 - `Int` - provide all digits optionally prefixed with `-`, for example `-378`
 - `Bool` - use one of predefined values - `true` or `false`

Additionally, special characters in string literals are escaped using backslash.

Apart from base types smooth contains also composite types -
arrays, structures and functions.
They work same way as in other languages. 
Let's explore each one in more detail.


#### Arrays

Array is a sequence of elements of the same type.
Array type is referenced by enclosing its element type inside brackets `[]`.
Array literal is declared enclosing comma separated elements (expressions) inside brackets `[]`.
Nth element of an array is accessed using [elem](api/elem.md) function.
Let's see everything we just explained in code below:

```
[Int] naturalNumbers = [1, 2, 3, 5, 7];
Int fourthNaturalNumber = elem(naturalNumbers, 4);
```


#### Structures

Structure is a composite of named values known as its fields with potentially different types.
Structures and its values are immutable (like all smooth values).
Structure type is defined by providing UpperCamelCase name
and parenthesized comma separated list of fields (named values).
Structure type is referenced by providing its name.
Structure field value is accessed using `.` operator.
Each Structure type has auto-generated constructor 
which is a function with the same name as a structure 
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

It is just file path and file content.
It is used by many functions from standard library, most popular are:
 - [file](api/file.md) - reads single file from project at given path
 - [files](api/files.md) - reads all files from given directory inside project
 - [filter](api/filter.md) - filters array of files according to glob pattern
 - [jar](api/jar.md) - jars array of files
 - [zip](api/zip.md) - zips array of files


#### Functions

Function is a self-contained block
that takes parameters and returns single result.
It can be polymorphic - more on that below.
Functions are first-class citizens and as such they can be:
 - passed to other functions as argument
 - returned from function as its result
 - stored as array elements or structure fields
 - assigned to named values

Let's refactor our initial example that compiles and jars java code,
by extracting handy `javaModule` function,
so it can be used for building different modules.

```
Blob javaModule(String path) = files(path) > javac() > jar();
commonsJar = javaModule("src/commons");
pluginsJar = javaModule("src/plugins");
```

Function type is constructed arrow syntax.
`(Int,Bool)->String` is type of function that takes `Int` and `Bool` and returns `String`.


#### parameter default value

When we define function we can provide default values for some parameters.
This way call to such function does not have to provide values for such parameters.
Consider [javac](api/javac.md) function.
So far we always called it with single argument.
However, if you inspect its documentation, you can see that it has multiple parameters.
Compiler allowed to pass only argument for first parameter
because all the others have default value.
Let's try passing values for first and second parameter:

```
[File] sources = files("src");
[File] libs = [file("lib/guava-31.0.1-jre.jar")];
release = javac(sources, libs) > jar();
```

We called [javac](api/javac.md) and provided array of third-party libraries that our code uses.
Note that currently smooth build does not have function that could download
library from maven repositories. This feature is high on wish list so stay tuned.

We can also simplify that code by using pipes.
Below call to [javac](api/javac.md) passes two arguments -
first is result of call to `[files](api/files.md)` piped through `>`,
second is value of `libs` passed directly.

```
[File] libs = [file("lib/guava-31.0.1-jre.jar")];
release = files("src") > javac(libs) > jar();
```

How about calling function and providing values for 1st and 4th parameter?
How compiler would know that 2nd argument should be assigned to 4th parameter?
It would not, so we need to write it explicitly using named arguments.
Below code calls [javac](api/javac.md) by providing value for first parameter,
and explicitly assigning value `"17"` to its 4th parameter - `target`.

```
[File] sources = files("src");
release = javac(sources, target="17") > jar();
```


#### Polymorphic functions

Smooth allows declaring polymorphic functions and values via
[parametric polymorphism](https://en.wikipedia.org/wiki/Parametric_polymorphism).
To define type variable simply use ALLCAPS name.
Below copy of [id](api/id.md) function declaration from [standard library](api.md)
that returns its only parameter.

```
A id(A a) = a;
```

When polymorphic function is invoked its actual type is inferred automatically.
```
Int result = id(7);
```


#### Lambdas

Lambda is unnamed function that can be defined and used inline.
It uses lambda syntax using arrow `->` similarly to other programming languages.
In following example we use [map](api/map.md) function
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


### Caching

If you run build command twice for our initial example

```
release = files("src") > javac() > jar();
```

you will notice that second evaluation completes almost instantly.
That's because result of each operation has been cached by smooth.
This looks like nothing extraordinary 
as most build tools reuse result from previous execution.
However, smooth cache system is more fine-grained
as it caches on disk result of each bytecode operation on given arguments.
When it has to evaluate given operation again it simply takes result from the cache.
We won't delve into details of smooth bytecode and its operations,
for now it is enough to state that every call to native function
(function that is implemented in java not in smooth)
is a bytecode operation.

You can see yourself how cache works by running build for our initial example,
then changing one of java files in `src` directory by adding empty
spaces to the end of some line and then running build again.
When you run build second time, you will notice that javac task
is re-executed (as content of *.java files has changed)
but because only formatting of the file changed,
compilation will produce exactly the same *.class files as before.
Smooth won't execute [jar](api/jar.md) function at all
as the result of such execution is already in its cache.
Console output will contain "cache" word at the end of the line
representing [jar](api/jar.md) function execution.
Note that such optimization is not possible with incremental building
as change to any file at the bottom of the build tree will always
force rebuild of all tasks that depend on it.

Now if you revert changes you introduced to mentioned java file
and run build once again then result will be instantaneous.
All function calls (function plus actual argument values) have been already executed
during previous build runs, so they are taken from cache.
The only exception is call to `files("src")` function which is impure
as it reads files from disk so its result cannot be cached between builds.

Such solution is powerful as it gives you access to any build result you have ever executed.
You just need to checkout relevant code from your repository
and run build command that will provide results instantly.


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

In every case where comma is used to separate list of language elements,
it may (or may not) be used also after last element.
For example following struct definition uses comma after last field

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
If you format your code so each such element is on separate line
and use trailing comma then it's much easier to reorder such elements.
Such a change is also more readable in version control.


### Things not yet implemented

Basic native functions:
 - `fold` function (`B fold([A] array, (A,B)->B func, B zero)`)
 - modules and imports so functions/values do not pollute global namespace
 - recursion
