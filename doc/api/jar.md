## jar

Jars an array of files.

 * File[] __files__ - files to be compressed (jared).
 * Blob __manifest__ - Content of manifest file.

Returns __Blob__ containing compressed files.

### examples

Jars all files from "src" directory.
```
app.jar = files("//src") | jar ;
```
