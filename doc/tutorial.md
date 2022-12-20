
### smooth-build tutorial

Smooth uses `build.smooth` file located in project's root directory as
a description of project's build process.
`build.smooth` files are written in (statically typed and functional) smooth language.
One of the simplest non-trivial build files is:

```
File release = projectFiles("src") | javac() | jar();
```

This script defines value `release` which body contains function calls separated by `|`.
Separator `|` means that expression on the left (of `|`) is passed as the first argument
to function call on the right. So in our example call to [projectFiles](api/projectFiles.md) 
function returns all files from `src` directory located at project's root.
That result is passed as argument to call to [javac](api/javac.md) function that invokes java 
compiler and compiles all the files.
Result returned from [javac](api/javac.md) call is then passed as argument to 
[jar](api/jar.md) function which compresses those files into a jar file.

You can build `release` (evaluate `release` value) from command line by running:

```
smooth build release
```

Above command evaluates `release` value and stores its result 
in `.smooth/artifacts/` directory as `release` file.
That location is printed to console at the end of build process:

```
Saving artifact(s)
  release -> '.smooth/artifacts/release'
```

Smooth is capable of inferring type of any expression, 
so we don't have to declare it explicitly.
Our initial example can be simplified to
```
release = projectFiles("src") | javac() | jar();
```
For educational reasons we will keep writing types explicitly in our examples.

If you want to try examples yourself then
[download and install smooth](install.md)
first.
You can consult
[standard library](api.md)
for a list of all available functions and values.
Currently, it contains functions related to building java projects but in the future
it can be easily extended to support other languages.

### Parallel execution

Smooth build evaluates values in parallel unless they depend on each other.
We can take a look at it using slightly more complicated example below.
Note that there's ugly duplicated code in this example.
We make it clean further in this tutorial for now we just focus on parallelism. 

```
File main = projectFiles("src-main") | javac() | jar();
File deps = projectFiles("src-deps") | javac() | jar();
```

As both functions (`main` and `deps`) do not depend on each other
then their bodies are executed in parallel. 
We do not need to do anything to make it happen.
It is enough to ask smooth to build those jars with `smooth build main deps`.

### Caching

If you run build command twice for our initial example

```
File release = projectFiles("src") | javac() | jar();
```

you will notice that second evaluation completes almost instantly.
That's because result of `release` evaluation has been cached by smooth.
This is nothing extraordinary as most build tools reuse result
from previous execution.
However, smooth is much smarter.
Its cache system is much fine-grained as it keeps on disk result
of each function call (function plus its arguments).
When it has to evaluate given call again it simply takes result from the cache.

You can see how it works by running build for our initial example,
then changing one of java files in `src` directory by adding empty
spaces to the end of some line and then running build again.
When you run build second time, you will notice that javac task
is re-executed (as content of *.java files has changed)
but because only formatting of the file changed,
compilation will produce exactly the same *.class files as before.
Smooth won't execute `jar` function at all as result of such execution
is already in its cache.
Console output will contain "cache" word at the end of the line
representing `jar` function execution.
Note that such optimization is not possible with incremental building
as change to any file at the beginning of the build pipeline will always
force rebuild of all tasks that depend on it.

Now if you revert changes you introduced to mentioned java file
and run build once again then result will be instantaneous.
All function calls (function plus actual argument values) have been already executed
during previous build runs so they are taken from cache.
The only exception is `projectFiles("src")` call which reads files from disk
so its result is never cached between builds.

Such solution is powerful as it gives you access to any build result you have ever executed.
You just need to checkout relevant code version from your repository
and run build command that will provide results instantly.

### Type system

Smooth language is statically typed which means all types are known at compile time.
It is also strongly typed, so it is not possible to assign value of one type
to value of different type.
First let's discuss all types available in smooth language.

#### Base types
Base types are predefined by the language (cannot be defined by user).
Currently, we have following base types: Bool, String, Int, Blob.

##### _Bool_
Boolean is a type with two values:
[true](api/true.md)
and
[false](api/false.md).


##### _String_
String is a sequence of characters.
String value can be defined using String literal,
which is a sequence of characters enclosed in double quotes.

```
String welcomeString = "Hello World";
```

##### _Int_
Int is an integer with arbitrary-precision - its size is not bound.
You can have Int value as big as you want unless it doesn't fit into memory.
Int value can be defined using Int literal,
which is a sequence of decimal digits optionally prefixed with minus sign (`-`).

```
Int favoriteNumber = 17;
Int enormousInt = 1234567890000000000;
```

##### _Blob_
Blob is a sequence of bytes.
Blob value can be defined using Blob literal,
which is a sequence of hexadecimal digits prefixed with `0x`.
Number of digits has to be even.
It is allowed to use both capital and small letters.

```
Blob binaryData = 0x48656C6C6F;
```

#### Struct types
Struct is a compound of named values known as its fields (like in most programming languages).
Each field may be of different type.
It is possible to define struct type in following way (comma after last field is not mandatory):

```
Person(
  String firstName,
  String lastName,
)
```

and obviously it is possible to use struct type as a field type in other struct.

```
Dog(
  String name,
  Person owner,
)
```

Definition of each struct automatically generates constructor for that struct.

```
Person person = person("John", "Doe");
```

As one can guess constructor of given struct is a function that
 - returns value of given struct type
 - has the same name as given struct with first letter lowercased
 - has parameter for each struct's field with the same name and type as that field

Apart from being automatically generated, constructor is an ordinary function and
behaves exactly like any other function.

Accessing specific field of struct value is done using dot `.`.

```
String name = person.lastName;
```

#### Array types
Array is an ordered sequence of elements. Each element has the same type.
Array value can be defined using array literal,
which is a comma separated sequence of values enclosed in square brackets `[]`.
Name of array type is name of its element type enclosed in square brackets.
Let's create array of `String`s:

```
[String] friends = [ "John", "Kate", "Alice" ];
```

It is possible to nest arrays without any limit.
Below example of two level deep array (array of arrays of `String`).

```
[[String]] groups = [ [ "circle" ], [ "triangle" ], [ "square", "rectangle" ] ];
```

### Functions

Let's look once again at `release` value that we defined at the beginning of this tutorial.

```
File release = projectFiles("src") | javac() | jar();
```

It uses function chaining (represented by pipe symbol `|`) to pass function call result as
argument to other function call.
In fact function chaining is just syntactic sugar for more standard function calls.
We can refactor above function definition to:

```
File release = jar(javac(projectFiles("src")));
```

This version is less readable despite being more familiar to people
coming from imperative languages.

We can define our own functions in `build.smooth` same way we defined values so far.
Let's refactor our initial example by splitting it into two functions and adding result types:

```
[File] classes(String sourcePath) = projectFiles(sourcePath) | javac();
File release = jar(classes("src"));
```

We defined function `classes` that takes one `String` parameter being path to source file dir
and compiles those files and returns as an array of `File`s. 

This way we can build our own set of reusable functions.
For example:

```
File javaJar(String srcPath) = projectFiles(srcPath) | javac() | jar();
File main = javaJar("src/main");
File other = javaJar("src/other"); 
```

#### Function parameter default value

When we define function we can provide default value for some parameters.
This way call to such function does not have to provide value for such parameters.

Let's create function that creates text file. 

```
File textFile(String text, String name = "file.txt") 
  = file(toBlob(text), name);
```

We can call it without specifying `name` parameter as it has default value:

```
File myFile = textFile("I love text files.");
```

but we can also override default argument by specifying value for `name` parameter:

```
File myFile = textFile("I love text files.", "secret.txt");
```

So far our example exercised default value of parameter that comes last on
parameter list. 
However, when we skip some argument(s) in the middle of the list
we need to inform compiler which are missing.
If argument listed on n-th place in function call should not be assigned
to parameter that is on n-th place in function signature then
we have to explicitly provide name of parameter to which it should be assigned.

For example function [javac](api/javac.md) 
from [standard library](api.md) which signature is:
```
[File] javac(
  [File] srcs,
  [File] libs = [],
  String source = "1.8",
  String target = "1.8",
  [String] options = [],
)
```

can be invoked as

```
[File] files = projectFiles("src");
[File] classes = javac(files, source="1.5");
```

### Polymorphism

Smooth allows declaring polymorphic functions and values via 
[parametric polymorphism](https://en.wikipedia.org/wiki/Parametric_polymorphism).
To define type variable simply use a name which all characters are uppercase.
Below declaration of [id](api/id.md) function from [standard library](api.md) 
that returns its only parameter.

```
A id(A a) = a;
```

When polymorphic function is invoked actual type are inferred automatically.
```
Int result = id(7);
```

Example of polymorphic value can be empty array:

```
[A] emptyArray = [];
```

### Trailing commas

In every case where comma is used to separate list of language elements,
it may (or may not) be used also after last element.
For example following struct definition uses comma after last field

```
Person(
  String firstName,
  String lastName,
)
```

but it is acceptable to skip it without changing semantics

```
Person(
  String firstName,
  String lastName
)
```

This works the same when defining function parameters, arguments in function call,
elements in array literal.
If you format your code so each such element is on separate line
and use trailing comma then it's much easier to reorder such elements.
Such a change is also more readable in version control.
