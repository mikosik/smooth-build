## file

Returns file read from project tree at given path.

 * String __path__ - Path in project tree to file.
Path should start with `//` which denotes root of project.
Referencing directory outside project is not allowed.
Slash `/` should be used as separator no matter what your operating system is.

Returns __File__ read from project tree at given path.

### examples

Returns file located at 'doc/contributors.txt' in project tree.
```
names = file("//doc/contributors.txt") ;
```
