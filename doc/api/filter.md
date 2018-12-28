## filter

Filters files according to given glob pattern.

 | Name | Type | Default | Description |
 | ---- | ---- | ------- | ----------- |
 | files | [File] |   | Array of files to be filtered. |
 | include | String |   | glob pattern specyfing which files should be filtered (included in result). Following wildcards are allowed: <ul><li> `**` matches any number (possibly zero) of directories.</li><li>`*` matches any number (possibly zero) of characters in file/directory name.</li></ul> |

Returns __[File]__ with filtered results.

### examples

Takes all files (recursively) from "pictures" directory and filters only those that end with "jpg" extension.

```
[File] jpgFiles = files("//pictures") | filter("**/*.jpg");
```
