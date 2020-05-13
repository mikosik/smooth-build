## files

Returns all files read recursively from project tree at given directory path.

 | Name | Type | Default | Description |
 | ---- | ---- | ------- | ----------- |
 | dir | String |   | Path to a directory relative to project dir (one that contains build.smooth). Referencing directory outside project by using `..` or leading `/` is not allowed. Slash `/` should be used as separator no matter what your operating system is. |

Returns __[File]__ with all files recursively from the directory specified via 'dir' parameter.

### examples

Returns all files located in 'src' directory or its subdirectories.
```
[File] sources = files("src") ;
```
