## concat

Concatenates an array of arrays.

| Name  | Type  | Default | Description     |
|-------|-------|---------|-----------------|
| array | [[a]] |         | array of arrays |

Returns __[a]__ containing all elements from array elements concatenated 

### examples

Returns array of all files from 'src/common' and 'src/main' directory.
```
[File] commonSource = projectFiles("src/common");
[File] mainSource = projectFiles("src/main");
[File] allSource = concat([commonSource, mainSource]);
```
