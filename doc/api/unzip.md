## unzip

Unzips files from given zip file.

 * Blob __blob__ - Zip to be uncompressed.

Returns __File[]__ - arrays of File uncompressed from given zip.

### examples
Uncompresses all files from "zips/myZip.zip" file.

```
files: file("//zips/myZip.zip") | unzip ;
```

