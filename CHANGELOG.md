Change Log
==========

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

