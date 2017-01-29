## filter

Filters files according to given glob pattern.

 * File[] __files__ - Array of files to be filtered.
 * String __include__ - glob pattern specyfing which files should be filtered
(included in result). Following wildcards are allowed:
   * `**` matches any number (possibly zero) of directories.
   * `*` matches any number (possibly zero) of characters in file/directory name.

Returns File[] with filtered results.

### examples

Takes all files (recursively) from "pictures" directory and filters only those that end with "jpg" extension.

```
jpgFiles = files("//pictures") | filter("**/*.jpg");
```
