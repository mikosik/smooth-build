## elem

Returns array element at given index.

| Name   | Type | Default | Description                |
|--------|------|---------|----------------------------|
| array  | [A]  |         | Array to read element from |
| index  | Int  |         | Index of element to read   |


### examples

Returns array of all files from 'src/common' and 'src/main' directory.
```
String firstElement = elem([ "first", "second", "third" ], 0);
```
