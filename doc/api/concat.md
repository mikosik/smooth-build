## [A] concat<A>([[A]] array)

Concatenates an array of arrays.

| Name  | Type  | Default | Description     |
|-------|-------|---------|-----------------|
| array | [[A]] |         | array of arrays |

Returns __[A]__ containing all elements from array elements concatenated 

### examples

Returns array of all files from 'src/common' and 'src/main' directory.
```
[File] commonSource = files("src/common");
[File] mainSource = files("src/main");
[File] allSource = concat([commonSource, mainSource]);
```
