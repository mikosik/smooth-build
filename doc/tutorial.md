### smooth-build tutorial

Smooth uses `build.smooth` file located in project's root directory as
a description of project's build process.
`build.smooth` files are written in (statically typed and functional) smooth language.
One of the simplest non trivial build files is:

```
release = files("//src") | javac | jar;
```

This script defines `release` function that does not have any parameters
and performs following tasks:

 * Invokes `files` function that takes all files (recursively)
 from `src` directory located at project's root. Double slashes `//` denote
 root directory of your project (directory in which given build.smooth file
 is located).
 * Passes them to `javac` function that compiles those files.
 * Passes compiled files to `jar` function that packs them into jar binary.

You can invoke evaluation of `release` function from command line by running:

```
smooth build release
```

Above command evaluates `release` function and stores its result 
in `.smooth/artifacts/` directory as `release` file.
That location is printed to console at the end of build process:

```
Saving artifact(s)
  release -> '.smooth/artifacts/release'
```

If you want to choose a different name for produced artifact file than default (= function name), 
you can do the following.

```
release = files("//src") | javac | jar | file("myApp.jar");
```

and it will produce following output during build:

```
Saving artifact(s)
  release -> '.smooth/artifacts/release/myApp.jar'
```

If you want to try examples yourself then
[download and install smooth](install.md)
first.
You can consult
[standard library](api.md)
for a list of all available functions.
Currently, it contains functions related to building java projects but in the future
it can be easily extended to support other languages.

### Parallel execution

By default, smooth build executes functions that do not depend on each other in parallel.
We can take a look at it using slightly more complicated example below.
Note that there's ugly duplicated code in this example.
We make it clean further in this tutorial for now we just focus on parallelism. 

```
main = files("//src-main") | javac | jar;
deps = files("//src-deps") | javac | jar;
```

As both functions (`main` and `deps`) do not depend on each other
then their bodies are executed in parallel. 
We do not need to do anything to make it happen.
It is enough to ask smooth to build those jars with `smooth build main deps`.

### Caching

If you run build command twice for our initial example

```
release = files("//src") | javac | jar;
```

you will notice that second run completes almost instantly.
That's because output from `release` function has been cached
by smooth.
This is nothing extraordinary as most build tools reuse result
from previous execution.
However smooth is much smarter.
Its cache system is much fine-grained as it keeps results
of each function call it has ever executed.
When it has to execute given call (function plus its arguments) again
it just takes result from cache.

You can see how it works by running build for our initial example,
then changing one of java files in `src` directory by adding empty
spaces to the end of some line and then running build again.
When you run build second time, you will notice that javac task
is re-executed (as content of *.java files has changed)
but because only formatting of the file changed,
compilation will produced exactly the same *.class files as before.
Smooth won't execute `jar` function at all as result of such execution
is already in its cache.
Console output will contain "cache" word at the end of the line
representing `jar` function execution.
Note that such optimization is not possible with incremental building
as change to any file at the beginning of the build pipeline will always
force rebuild of all tasks that depend on it.

Now if you revert changes you introduced to mentioned java file
and run build once again then result will be instantaneous.
Except `files("//src")` call which reads files from disk and its result is never cached
all other functions have been executed before with arguments they receive in that run so
instead of running them smooth will read results from cache.
Such solution is powerful as it gives you access to any build result you have ever executed.
You just need to checkout relevant code version from your repository
and run build command that will provide results instantly.

### Type system

Smooth language is statically typed which means all types are known at compile time.
It is also strongly typed so it is not possible to assign value of one type
to value of different type unless the former is convertible to the latter.
We will come back to topic of conversion later.
First let's discuss all types available in smooth language.

#### Basic types
Basic types are predefined by the language (cannot be defined by user).
Currently, we have three basic types: String, Bool, Blob, Nothing.
Others (like Int) will be added before smooth reaches version 1.0.

##### _Bool_
Boolean value that can be either `true` or `false`.
The only instances of that type are returned by functions
[true](api/true.md)
and
[false](api/false.md).

##### _String_
String is a sequence of characters.
String value can be defined in-line using String literal,
which is a sequence of characters enclosed in double quotes.

```
String welcomeString = "Hello World";
```

##### _Blob_
Blob is a sequence of bytes.
There's no literal for creating Blobs (yet).

##### _Nothing_
Nothing is a type that is convertible to any type.
It is not possible to create value of such type.
This sounds strange but reasons for such type are explain below when discussing array types.

#### Struct types
Struct is a compound of named values known as its fields (like in most programming languages).
Each field may be of different type.
It is possible to define struct type in following way (comma after last field is not mandatory):

```
Person {
  String firstName,
  String lastName,
}
```

and obviously it is possible to use struct type as a field type in other struct.

```
Dog {
  String name,
  Person owner,
}
```

Definition of each struct automatically generates constructor for that struct.
Constructor of given struct is a function that
 - returns value of given struct type
 - has the same name as given struct with first letter lowercased
 - has parameter for each struct's field with the same name and type as that field

Let's create some value of type Person which we defined above as struct.

```
Person person = Person("John", "Doe");
```

Apart from being automatically generated, constructor is an ordinary function and
behaves exactly like any other function.

Accessing specific field of struct value is done using dot `.`.

```
String name = person.lastName;
```

Each struct value can be automatically converted to value of its first field.
This happens when we invoke a function passing argument of some struct type
for parameter that has type equal to type of struct's first field.


Most common struct is predefined `File` struct. It is defined as:

```
File {
  Blob content,
  String path,
}
```

First field of `File` is `content` with type `Blob` which means `File` value can
be converted to `Blob`.

#### Array types
Array is an ordered sequence of elements. Each element has the same type.
Array value can be defined in-line using array literal,
which is comma separated sequence of values enclosed in square brackets.
Name of array type is name of its element type enclosed in square brackets.
Let's create array of `String`s:

```
[String] friends = [ "John", "Kate", "Alice" ];
```

It's possible to nest arrays without any limit.
Below example of two level deep array (array of arrays of `String`).

```
[[String]] groups = [ [ "circle" ], [ "triangle" ], [ "square", "rectangle" ] ];
```

Arrays are [covariant](https://en.wikipedia.org/wiki/Covariance_and_contravariance_(computer_science)) which means that if value of type `A` can be assigned to value of type `B`, then array of type `[A]` (array of elements of type A) can be assigned to array of type `[B]`.

One corner case of array types is empty array.
As empty array does not hold any elements, it raises question what is the type of such empty array.
Solution is pretty simple - empty array has type `[Nothing]`.

```
[Nothing] emptyArray = [];
```

Based on the fact that arrays are covariant and Nothing type is convertible to any other type,
then empty array is assignable to any other array.

```
[Nothing] emptyArray = [];
[String] strings = emptyArray;
```

#### Generic types

Smooth allows declaring function with generic parameters. To define generic
parameter simply use single upper case letter as its name.
Below declaration of `identity` function that returns its only parameter.

```
A identity(A value) = value;
```

#### Type inference

Smooth is capable of inferring type of any expression so we don't have to declare it explicitly.
All our examples so far had type declared explicitly for educational reasons.
In everyday code we can simply skip type and replace definitions like:

```
[String] strings = [ "dog", "cat", "donkey" ];
```

with equal version

```
strings = [ "dog", "cat", "donkey" ];
```


### Functions

Let's look once again at `release` function we defined at the beginning of this tutorial.

```
release = files("//src") | javac | jar;
```

It uses function chaining (represented by pipe symbol `|`) to pass function call result as
argument to other function call.
In fact function chaining is just syntactic sugar for more standard function calls.
We can refactor above function definition to:

```
release = jar(javac(files("//src")));
```

This version is less readable despite being more familiar to people
coming from imperative languages.

Functions declared in `build.smooth` (for example `release`)
can be used the same way as standard library functions (like `javac`).
We can refactor our initial example by splitting it into two functions and adding result types:

```
[File] classes(String sourcePath) = files(sourcePath) | javac;
File release = jar(classes("//src"));
```

This way we can build our own set of reusable functions.
For example:

```
javaJar(String srcPath) = files(srcPath) | javac | jar;
main = javaJar("src/main");
other = javaJar("src/other"); 
```

#### Function parameter default value

When we define function parameter we can provide default value for some of them.
This way call to such function does not have to provide value for such parameters.
The only limitation is that all parameters with default values must be placed
after parameters without default values.

Let's create function that creates text file:

```
File textFile(String text, String name = "file.txt") = File(toBlob(text), name);
```

We can call it without specifying `name` parameter as it has default value:

```
File myFile = textFile("I love text files.");
```

but we can also override default value by specifying value for `name` parameter:

```
File myFile = textFile("I love text files.", "secret.txt");
```

If a function has more than one parameter with default value and we want to specify
value for only some default parameters we can select that parameters by prefixing
argument with parameter name and equal sign `=`. For example:

```
File myFile = textFile("I love text files.", name="secret.txt");
```

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
and use trailing comma
then it's much easier to reorder such elements.
Such a change is also more readable in version control.
