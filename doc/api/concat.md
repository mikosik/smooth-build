## concatenate

Concatenates two arrays.

 | Name   | Type | Default | Description   |
 | ------ | ---- | ------- | ------------- |
 | array1 | [a]  |         | first array.  |
 | array2 | [a]  |         | second array. |

Returns __[a]__ containing all elements from first and second array.

### examples

Returns array of all files from 'src/common' and 'src/main' directory.
```
[File] commonSource = files("//src/common");
[File] mainSource = files("//src/main");
[File] allSource = concat(commonSource, mainSource);
```
