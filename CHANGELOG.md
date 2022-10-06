Change Log
==========

Version 0.21.0 (??????????)
---------------------------

 * redesigned type parameter name: it a string containing only capital letters from A..Z range

Version 0.20.0 (2022.10.05)
---------------------------

 * changed smooth language: added `Int` type
 * added slib functions: `map`, `id`, `flatten`, `jarFile`
 * added `a`, `d`, `n` to `--show-tasks` option in `smooth build` command as shortcuts to `all`, `default`, `none`
 * added `field`, `reference`, `map` matchers to `--show-tasks` option in `smooth build` command
 * changed smooth language: added PURE/IMPURE attribute to @Native annotation
 * changed smooth language: parameters with default arguments are no longer required to be placed at the end of parameter list
 * changed smooth language: removed automatic conversions
 * changed smooth language: function call must have parentheses
 * renamed slib functions: aFile -> projectFile, files -> projectFiles
 * changed smooth language: introduced global values
 * changed smooth language: allowed pipe as default argument
 * changed smooth language: allowed pipe as element of array literal
 * changed smooth language: added parametric polymorphism

Version 0.19.0 (2020.08.03)
---------------------------

 * added Blob literals to smooth language
 * changed smooth language to allow defining struct with `Nothing` field
 * added --project-dir command line option
 * only one smooth process can be run at the same time for given project (synchronized via ./smooth/lock)
 * redesigned format of path parameter in 'files' and 'aFile' functions to not contain leading '//'
 * improved 'filter' function to support more glob wildcards
 * renamed 'concatenate' function to 'concat'
 * added parallel execution
 * removed 'javaProperty' function from standard library (aka platform API)
 * extended parser errors with source code where problematic part is marked
 * renamed 'smooth dag' command to 'smooth tree'
 * fixed 'equal' function so both parameters have the same generic type 
 * changed generic type names from one small letter to one large letter
 * changed default constructor name to be equal to struct name with lowercased first letter
 * renamed 'file' function to 'aFile' in platform API
 * added --log-level option to most commands
 * added --show-tasks option to build command

Version 0.18.0 (2019.03.22)
---------------------------

 * suppressed 'illegal reflective access' warnings from java-11 runtime
 * added 'and', 'or', 'not' functions to platform API
 * added 'equal' function to platform API
 * added 'if' function to platform API

Version 0.17.0 (2019.01.06)
---------------------------

 * added 'Bool' type to smooth language
 * added 'concatenate' function to platform API (removed non generic versions)
 * redesigned argument assignment from implicit/explicit to positional/named
 * added generics to smooth language

Version 0.16.0 (2018.03.14)
---------------------------

 * added user defined structure type to smooth language

Version 0.15.0 (2018.01.23)
---------------------------

 * allowed nested arrays in smooth language
 * bugfix: conversion from Nothing to any smooth type should be possible
 * disallowed '.' and '-' characters in function and parameter names
 * added 'deps' parameter to 'junit' function and require junit implementation to be passed via it
 * added 'options' parameter to 'javac' function in platform API
 * build artifacts are reported to console

Version 0.14.0 (2017.11.20)
---------------------------

 * added 'smooth list' subcommand
 * added 'smooth version' subcommand
 * native function implementation must be also declared in smooth module using smooth language
 * @Required is no longer used in native function implementation as its implied when parameter doesn't have default value specified

Version 0.13.0 (2017.09.26)
---------------------------

 * defined functions can have result type specification
 * defined functions can have parameters (previously only native functions could) and they can have default values specified
 * fixed smooth script to work correctly when it is redirected to via symlink
 * established array type as basic type inside brackets '[]'
 * changed ':' to '=' in function declaration

Version 0.12.0 (2016.12.14)
---------------------------

 * fixed 'junit' function implementation so it correctly sets context classloader
 * fixed 'unzip' and 'unjar' functions to correctly report corrupted archives
 * renamed 'toFile' to 'File'
 * nothing is allowed type for parameter and function result

Version 0.11.0 (2015.12.06)
---------------------------

 * added java 1.8 as a run-time requirement
 * changed format of 'path' argument to 'file' and 'files' functions
 * renamed 'concatenateStrings' to 'concatenateStringArrays'
 * renamed 'concatenateFiles' to 'concatenateFileArrays'
 * renamed 'concatenateBlobs' to 'concatenateBlobArrays'
 * extended 'javac' function by adding support to "1.8"/"8" values to 'source' and 'target' parameters
 * removed support for Windows OS
 * removed 'jarjar' function, jarjar project looks abandoned and doesn't have support for java 8
 * missing arguments are initialized to default value of their type
 * added 'concatenateStrings' function

Version 0.10.0 (2014.03.25)
---------------------------

 * added 'toString' function
 * added 'jarjar' function
 * fixed bug #7 in command line interface by switching from airline to argparser4j lib

Version 0.9.1 (2014.03.03)
--------------------------

 * fixed bug in array literals

Version 0.9.0 (2014.01.18)
--------------------------

 * added 'aidl' function for running android aidl tool
 * fixed "Too many open files" bug caused by not closing all streams correctly

Version 0.8.0 (2013.12.13)
--------------------------

 * command line 'smooth build' without functions will print available functions
 * added 'path()' function returning File path
 * removed undocumented 'newFile()' function
 * added 'toBlob()' function
 * added 'toFile()' function for converting Blob + path -> File
 * renamed 'toBlob()' function to 'content()'

Version 0.7.0 (2013.12.10)
--------------------------

 * added 'include' param to junit function
 * 'junit' function reports warning when no test is found

Version 0.6.0 (2013.12.02)
--------------------------

 * added concatenateFiles, concatenateBlobs smooth functions
 * removed merge smooth function
 * added toBlob smooth function
 * added automatic conversions File -> Blob, and File[] -> Blob[]
 * replaced collection types (File*, String*, Blob*) with array types (File[], String[], Blob[])
 * added Blob type
 * allowed dashes ('-') in function names

Version 0.5.0 (2013.11.16)
--------------------------

 * allowed dots ('.') in function names
 * artifacts returned by function passed to build command are stored in '.smooth/results' dir
 * removed smooth 'save' function

Version 0.4.0 (2013.11.08)
--------------------------

 * added build/clean commands to smooth binary
 * added task result caching based on content digest
 * improved formatting/content of user visible messages
 * added single line comments (comment starts with '#' character)

Version 0.3.0 (2013.10.08)
--------------------------

 * added 'filter' function for filtering File* according to pattern
 * added 'source' and 'target' parameters to 'javac' function

Version 0.2.0 (2013.10.01)
--------------------------

 * added 'junit' function
 * added 'output' parameter to 'zip' and 'jar' functions
 * added 'merge' functions for merging two File*

Version 0.1.0 (2013.09.23)
--------------------------

 * initial release

