## unjar

Unjars files from given jar file.

| Name | Type | Default | Description             |
|------|------|---------|-------------------------|
| jar  | Blob |         | Jar to be uncompressed. |

Returns __[File]__ - arrays of File uncompressed from given jar.

### examples
Uncompresses all files from "jars/myJar.jar" file.

```
[File] fileFromZip = file("jars/myJar.jar").content > unjar();
```

