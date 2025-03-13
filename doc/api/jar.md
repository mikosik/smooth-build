## Blob jar<>([File] files, Blob manifest)

Jars an array of files.

| Name     | Type   | Default | Description                     |
|----------|--------|---------|---------------------------------|
| files    | [File] |         | Files to be compressed (jared). |
| manifest | Blob   | 0x      | Content of manifest file.       |

Returns __Blob__ containing compressed files.

### examples

Jars all files from "src" directory.
```
Blob app_jar = files("src") > jar();
```
