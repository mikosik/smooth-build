## files

Returns all files read recursively from project tree at given directory path.

 | Name | Type | Default | Description |
 | ---- | ---- | ------- | ----------- |
 | dir | String |   | Path in project tree to a directory. Path should start with `//` which denotes root of project. Referencing directory outside project is not allowed. Slash `/` should be used as separator no matter what your operating system is. |

Returns __[File]__ with all files recursively from directory specified
via 'dir' parameter.

### examples

Returns all files located in 'src' directory or its subdirectories.
```
[File] sources = files("//src") ;
```
