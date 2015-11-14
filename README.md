Smooth-Build
------------

Smooth-build is a build tool.

Prerequisites
-------------

Learning is much faster when you can experiment on the way.
[Download and install](https://github.com/mikosik/smooth-build/blob/master/doc/install.md)
smooth-build so you can try examples from this tutorial yourself.
All examples listed below are available online at
[smooth-build-examples](https://github.com/mikosik/smooth-build-examples)
as github project.

Running build
-------------

Smooth uses `build.smooth` file located in project's root directory as
a description of project's build process.
If you downloaded
[smooth-build-examples](https://github.com/mikosik/smooth-build-examples)
repository, note that it contains set of exampleXX directories.
Each directory is a separate project so each contains single `build.smooth` file.

To run build process enter directory `example01` and type `smooth build release.jar`.
If you installed smooth correctly and added it to your system PATH as advised by
[download / install](https://github.com/mikosik/smooth-build/blob/master/doc/install.md)
you should get smooth running successfully with output equal to the one below:

```
$smooth build release.jar
 + files                                           [ line 2 ]
 + javac                                           [ line 2 ]
 + jar                                             [ line 2 ]
 + SUCCESS :)
$
```

Function calls
--------------

Now let's take a look at build file we've just run from
[example01](https://github.com/mikosik/smooth-build-examples/tree/master/example01).

```
release.jar: files("//src") | javac | jar ;
```

If you happen to know how pipes work in Linux shells
it will be clear to you what this script does.
It defines `release.jar` function that performs following tasks:

 * takes all files (recursively) from `src` directory
 * passes them to `javac` function (Java compiler) that compiles those files
 * packs compiled *.class files into jar file

Note that you don't need to explicitly create directory for *.class files
nor even mention it in build file, smooth handles that automatically.
All final artifacts are stored inside `.smooth/artifacts` directory.
In example above `.smooth/artifacts/release.jar` file containing *.class
files will be created.

Caching
-------

If you happened to run build command twice in previous example you should notice
that most of lines in output have `CACHE` word appended.
It means that those tasks were not executed in this run but their results were
taken from cache that keeps all results of all tasks run so far.

```
$smooth build release.jar
 + files                                           [ line 2 ]
 + javac                                           [ line 2 ] CACHE
 + jar                                             [ line 2 ] CACHE
 + SUCCESS :)
$
```

It may seem obvious so far.
Most build systems reuse results from previous runs.
However they do it via incremental building which creates problems of its own.
For example if you happen to delete one of your *.java files you have to
remember to do so called "clean" to remove *.class file that was generated
by previous run for that build.
Another thing is that icremental building remembers only output from
last execution of the build process.

Smooth-build is much smarter.
It maintains cache of results of all function calls it has ever executed.
If it ever has to execute given call (function plus its arguments) again
it just takes result from cache.

Let's see how it works by  changing one of java files in example01 project
(for example src/KnightsWhoSayNi.java) by adding a few empty spaces
at the end of one line.
When you run build again you notice that javac task has to be reexecuted (as content of *.java files has changed) but because
only formatting of the file changed, compilation produced exactly the same *.class
file as before.
When smooth tries to jar that file it will realize it has result
of such execution in its cache and will simply return it.
Note that such optimization is never possible with incremental building
as change to any file at the beginning of the build pipeline will always
force rebuild of all tasks that depend on it.

```
$smooth build release.jar
 + files                                           [ line 2 ]
 + javac                                           [ line 2 ]
 + jar                                             [ line 2 ] CACHE
 + SUCCESS :)
$
```

Now if you revert changes you introduced to mentioned java file
and run build once again then all task's result will be taken from cache.
If you happen to use git for versioning your source code
you can have a perfect marriage.
You can quickly switch between git branches and quickly have code built
at given branch.
This is possible as smooth cached all artifacts you built so far
so you can switch between branches freely.

Type system
-----------

Each value that is returned by a function or passed to a function as a parameter
can have one of types described below:
**String** - Sequence of characters
**Blob** - Sequence of bytes
**File** - Single file - compound object that has a content (Blob) and path associated with it (String).
**String[]** - Array of Strings
**Blob[]** - Array of Blobs
**File[]** - Array of Files

There are obviously some important types (like Boolean) missing.
They will be added before smooth-build reaches version 1.0.

Nested function calls
---------------------

Let's dig a little bit deeper into function chaining.
Chaining function calls via pipes that we've seen in the very first example
is just a syntactic sugar over more traditional function calls.
Let's take a look at
[example02](https://github.com/mikosik/smooth-build-examples/tree/master/example02)
that declares the same process but without pipes.

```
release.jar: jar(javac(files("//src"))) ;
```

Now it looks more friendly if you come from world of imperative languages.
As you could probably noticed, version using pipes is much more readable.
The flow of data through different functions is much more visible.
As a rule of thumb you should always strive to use pipes wherever possible.

Named parameters
----------------

If you take a look at our last example and check documentation for javac function
you notice that we passed only one argument to it while documentation
specifies more than one parameter.
It is possible because smooth does not require specifying values for all parameters.
If some paramter has no value assigned then it receives default value that depends on its type.
It's empty string for String parameters, empty stream for Blob and empty array for Array parameters.

Smooth doesn't require specifying names nor it requires to pass them in any order.
Smooth intelligently deduces which parameter should be assigned from which argument based on their types.
If there is ambiguity (for example two parameters have the same type as passed argument) matching will fail
unless one of two cases occurs:
 * argument is explicitly assigned to some parameter
 * one of parameters (with type equal to argument type) is marked as required, in that case it will be assigned

Let's look at [example03](https://github.com/mikosik/smooth-build-examples/tree/master/example03). It explicitly assigns arguments to parameters (although it is not necessary in that case as automatic assignment would work fine).

```
release.jar: jar(files=javac(sources=files(dir="//src"))) ;
```

Note that explicitly assigning argument to parameter cannot be done for value that is passed through a pipe. This may be a nuisanance as you have to use nested function calls in such cases.

Literals
--------
As you noticed Smooth contains String literals as any other language.
Another literal that you find useful is an array literal that lets you
create `String[]`, `Blob[]` and `File[]`.
Array literal is comma separated list of expressions enclosed inside brackets `[]`.
[example04](https://github.com/mikosik/smooth-build-examples/tree/master/example04)
pasted below shows array literal in action.
Note that this time our (user defined) function `zipped` calls other function
defined by us `books`.

```
books: [ file("//books/LifeOfBrian.txt"), file("//books/TheMeaningOfLife.txt") ] ;
zipped: books | zip ;
```

Advanced example
----------------

Let's do something more complicated.
Take a look at
[example05](https://github.com/mikosik/smooth-build-examples/tree/master/example05).

```
library: files("//src/lib") | javac | jar ;
release.jar: files("//src/main") | javac(libs=[library]) | jar ;
```

First line defines `library` - function that takes java files from `src/lib` dir,
compiles them and packs as jar file.
Second line does similar thing to java files from `src/main` dir
but it uses jar produces by the first line.

[core functions API](https://github.com/mikosik/smooth-build/blob/work/doc/api.md)
| [development blog](http://smooth-build.blogspot.com/)
