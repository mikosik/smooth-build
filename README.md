Smooth-build
============

Smooth-build is a build automation tool with simple and flexible (functional like) language and robust runtime.

You can compile and jar your java application with just one liner like this one:

```

release-jar: files("src/") | javac | jar | save("release/");

```

Note features that you have for free:
* No need to specify output directories for class files produced by javac function. They will be created automatically.
* No need need to create 'release' dir. Smooth-build knows you want it to be created so it creates one for you.


Soon following features will be added
=====================================

Below is the list of features that are not yet in place. Ones that are most important (will be probably implemented first) are at the top of the list.


Parallel function execution 
---------------------------

In the following Smooth script:
```
classes:     files("src/") | javac;
release-jar: classes | jar | save("release/");
test:        classes | junit;
```
functions `release-jar` and `test` both depend on `classes` function (which will be executed first) but do not depend on each others so they will be executed in parallel.


Incremental builds
------------------

Yikes! It is not supported yet but it is at the top of my to-do list. For now, each build starts from scratch.


Copy-paste detection
--------------------

Consider following Smooth script:
```
release-jar: files("src/") | javac | jar | save("release/");
test:        files("src/") | javac | junit;
```
Both `release-jar` and `test` functions compile sources from 'src' dir. Smooth build will detect that and perform only one compilation, feeding result to both functions. Obviously in most cases you would extract common code into separate function anyway (to increase readability and ease maintenance).


Parallelizing batches
---------------------

Processing set of files independently is a great occassion for parallelizing work. Consider process of converting svg files into png format. All we need is a function that accepts single svg file and returns single png file. Given this we should be able to run conversion of all files in parallel like in script below. Note double pipe characters ('||') before svgToPng call. This tells Smooth that although function svgToPng does not accept set of files but just a single file - each file should be passed to the function in separate call (of course call executions will happen in parallel).

```
convert: files("svg-files/") || svgToPng | save("release/");
```


Packages/Imports
----------------

Imports will be introduced to avoid name clashes when number of builtin functions grows. To use (reference) a function you would have to import it at the beginning of Smooth script file. You could also use fully qualified name in a function call.

```
import smooth.file.*;
import smooth.java.javac;

release-jar: files("src/") | javac | smooth.compress.jar | save ("release/");
```


User defined parameterized functions
------------------------------------

So far only builtin function could accept parameters. User defined functions were always paremeterless. Introduction of user defined parameterized functions will allow more advanced code reused. Consider following script:

```
release-app1-jar: files("app1/src/") | javac | jar | save ("release-app-1/");
release-app2-jar: files("app2/src/") | javac | jar | save ("release-app-2/");
```

The only difference between functions is a list of input files and output file. We could do better:

```
release-function(File* sources): sources | javac | jar;
release-app1-jar: files("app1/src/") | release-function | save ("release-app-1/");
release-app2-jar: files("app2/src/") | release-function | save ("release-app-2/");
```

This is not the shortest version posssible as I wanted to show how user defined function could be easily used inside pipes (taking output of one function, processing it, and passing to the next function in the pipe). We can ofcourse simplify it even more:

```
release-function(String sourceDir, String outputDir): files(sourceDir) | javac | jar | save(outputDir);
release-app1-jar: release-function("app1/src/", "release-app-1/");
release-app2-jar: release-function("app2/src/", "release-app-2/");
```


Modules
-------

As project gets bigger it is sometimes necessary to split build script into a few separate files. The simplest way I can think of is to treat each script file as a separate module with explicitely declared package name. Modules should be capable of installing other modules and importing functions from installed modules. This will be a heaven for 'convention over configuration' band. It will be not only possible to reuse project file structure that has been defined by others in some Smooth module simply by installing given module and calling its functions. It will be also possible to define your module and share it with others. As modules will be written entirely in Smooth language it will be easy to understand what they do and what file structure they require without digging in the documentation.


Other basic types
-----------------

Apart from following basic types:
* File - single file
* File* - set of files
* String - string value
* String* set of Strings

Smooth should support at least:
* Bool - boolean value
* Bool* - set of Bools
* Int - single integer
* Int* - set of integers


Module/function plugins
-----------------------

It should be possible to implement your own Smooth plugin (module with set of functions) in Java language and install it in your smooth module similar way it is done for Smooth modules/scripts. All builtin smooth functions are already implemented as plugins. The missing part is to provide a way to install/reference user developed plugins from Smooth modules.


