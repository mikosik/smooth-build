## file

Returns file read from project tree at given path.

| Name | Type   | Default | Description                                                                                                                                                                                                                       |
|------|--------|---------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| path | String |         | Path in project to a file. It is resolved against project dir . Referencing directory outside project by using `..` or leading `/` is not allowed. Slash `/` should be used as separator no matter what your operating system is. |

Returns __File__ read from project tree at given path.

### examples

Returns file located at 'doc/contributors.txt' in project tree.
```
File contributors = file("doc/contributors.txt") ;
```
