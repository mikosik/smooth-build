## zip

Zips an array of files.

 * File[] __files__ - files to be compressed (zipped).

Returns __Blob__ containing compressed files.

### examples

Zips all files from "src" directory.
```
files.zip = files("//src") | zip ;
```
