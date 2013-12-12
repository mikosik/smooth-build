Change Log
==========

Version 0.8.0 (??????????)
--------------------------

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

