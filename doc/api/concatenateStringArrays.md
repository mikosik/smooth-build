## concatenateStringArrays

Concatenates two [String].

 | Name | Type | Default | Description |
 | ---- | ---- | ------- | ----------- |
 | strings | [String] |   | Array of Strings to be concatenated with array passed via "with" parameter. |
 | with    | [String] |   | Array of Strings to be concatenated with array passed via "blobs" parameter. |

Returns __[String]__ containing all Strings from first and second argument.

### examples

Returns array of all Strings combined from 'first' and 'second' array.
```
first = [ "abc", "def" ] ;
second = [ "ghi", "jkl" ] ;
all = concatenateStringArrays(first, with=second);
```
