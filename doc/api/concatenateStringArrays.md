## concatenateStringArrays

Concatenates two String[].

 * String[] __strings__ - Array of Strings to be concatenated with array passed
via "with" parameter.
 * String[] __with__ - Array of Strings to be concatenated with array passed
via "blobs" parameter.

Returns __String[]__ containing all Strings from first and second argument.

### examples

Returns array of all Strings combined from 'first' and 'second' array.
```
first = [ "abc", "def" ] ;
second = [ "ghi", "jkl" ] ;
all = concatenateStringArrays(first, with=second);
```
