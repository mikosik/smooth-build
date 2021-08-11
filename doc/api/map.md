## map

Returns array created by applying `function` to each element of `array`.

| Name | Type | Default | Description |
 | ---- | ---- | ------- | ----------- |
| array | [E] |   | Array with elements to map. |
| function | R(E) |   | Function to apply to each element of an array. |

Returns array created by applying `function` to each element of `array`.

### examples

Converts array of files to array of paths of those files.

```
[File] files = projectFiles("src/java");
String pathOf(File file) = file.path;
[String] fileNames = map(files, pathOf);
```



