## Blob zip<>([File] files)

Zips an array of files.

| Name  | Type   | Default | Description                      |
|-------|--------|---------|----------------------------------|
| files | [File] |         | Files to be compressed (zipped). |

Returns __Blob__ containing compressed files.

### examples

Zips all files from "src" directory.
```
Blob zippedFiles = files("src") > zip();
```
