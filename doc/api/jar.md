## jar

Jars an array of files.

 | Name | Type | Default | Description |
 | ---- | ---- | ------- | ----------- |
 | files | [File] |   | Files to be compressed (jared). |
 | manifest | Blob | File("META-INF/MANIFEST.MF", toBlob("")) | Content of manifest file. |

Returns __Blob__ containing compressed files.

### examples

Jars all files from "src" directory.
```
Blob app_jar = projectFiles("src") | jar ;
```
