## concatenateFileArrays

Concatenates two [File].

 | Name | Type | Default | Description |
 | ---- | ---- | ------- | ----------- |
 | files | [File] |   | Array of Files to be concatenated with array passed via "with" parameter. |
 | with  | [File] |   | Array of Files to be concatenated with array passed via "files" parameter. |

Returns __[Files]__ containing all Files from first and second argument.

### examples

Returns array of all Files from 'src/common' or 'src/main' directories.
```
commonSource = files("//src/common");
mainSource = files("//src/main");
allSource = concatenateFileArrays(files=commonSource, with=mainSource);
```
