## unzip

Unzips files from given zip file.

 | Name | Type | Default | Description |
 | ---- | ---- | ------- | ----------- |
 | blob | Blob |   | Zip to be uncompressed. |

Returns __[File]__ - arrays of File uncompressed from given zip.

### examples
Uncompresses all files from "zips/myZip.zip" file.

```
[File] files = projectFile("zips/myZip.zip") | unzip ;
```

