## unjar

Unjars files from given jar file.

 * Blob __jar__ - Jar to be uncompressed.

Returns __[File]__ - arrays of File uncompressed from given jar.

### examples
Uncompresses all files from "jars/myJar.jar" file.

```
files = file("//jars/myJar.jar") | unjar() ;
```

