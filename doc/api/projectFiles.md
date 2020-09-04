## projectFiles

Returns all files read recursively from the directory specified by `dir` parameter.

 | Name | Type | Default | Description |
 | ---- | ---- | ------- | ----------- |
 | dir | String |   | Path to a directory relative to project dir (one that contains build.smooth). Slash `/` should be used as a separator no matter what your operating system is. Referencing directory outside project by using `..` or leading `/` is not allowed. Referencing project dir via `.` is allowed but result won't contain any files from `.smooth` directory. |

Returns __[File]__ read from directory specified by `dir` parameter.

### examples

Returns all files located in 'src' directory or its subdirectories.
```
[File] sources = projectFiles("src") ;
```
