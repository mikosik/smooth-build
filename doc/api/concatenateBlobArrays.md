## concatenateBlobArrays

Concatenates two Blob[].

 * [Blob] __blobs__ - Array of blobs to be concatenated with array passed
via "with" parameter.
 * [Blob] __with__ - Array of blobs to be concatenated with array passed
via "blobs" parameter.

Returns __[Blob]__ containing all Blob from first and second argument.

### examples

Returns array of all Blobs each being a file from 'src/common' or 'src/main'
directory.
```
commonSource = files("//src/common");
mainSource = files("//src/main");
allSource = concatenateBlobArrays(blobs=commonSource, with=mainSource);
```
