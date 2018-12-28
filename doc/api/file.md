## file

Returns file read from project tree at given path.

 | Name | Type | Default | Description |
 | ---- | ---- | ------- | ----------- |
 | path | String |   | Path in project tree to file. Path should start with `//` which denotes root of project. Referencing directory outside project is not allowed. Slash `/` should be used as separator no matter what your operating system is. |

Returns __File__ read from project tree at given path.

### examples

Returns file located at 'doc/contributors.txt' in project tree.
```
File names = file("//doc/contributors.txt") ;
```
