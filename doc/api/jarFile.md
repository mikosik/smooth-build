## jarFile

Creates jar with (compresses) files.

| Name     | Type   | Default | Description                     |
|----------|--------|---------|---------------------------------|
| files    | [File] |         | Files to be compressed (jared). |
| path     | String |         | Name of file to be created      |
| manifest | Blob   | 0x      | Content of manifest file.       |

Returns __File__ containing compressed files.

### examples

Creates jar file with all files from "src" directory.

```
File app_jar = files("src") > jarFile("src.jar") ;
```
