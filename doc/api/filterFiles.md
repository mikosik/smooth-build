## [File] filterFiles<>([File] files, String pattern)

Filters files according to given glob pattern.


Following wildcards in glob pattern are supported 
  (wildcards never match path separator `/`, except for `**` wildcard):
 * `?` matches single character
 * `[` `]` brackets contains expression that matches single character. 
   Expression can be:
    * set of characters (for example `[abc]`) then expression matches any character from that set
      (`a`, `b` or `c` in that example).
    * range of characters (for example `[a-c]`) then expression matches any character in that range
      (`a`, `b` or `c` in that example).
    * mix of two above (for example `[a-cxyz]` matches `a`, `b`, `c`, `x`, `y` or `z`)

    Character `!` negates expression when it is placed after opening bracket `[` 
    (for example `[!abc]` matches any character, except `a`, `b` or `c`)

    Character `-` matches itself when it is placed after opening bracket `[` 
    OR after `!` that is placed after opening bracket `[`

    Characters `?`, `*`, `\` are not treated as wildcards within brackets and match themselves.
 
 * `{` `}` braces contain group of subpatterns separated by comma `,`.
   Group matches if any subpattern matches.
   For example `file.{txt,md}` matches `file.txt` and `file.md`.
   Groups cannot be nested.
   
 * `*` matches zero or more characters
 * `**` matches zero or more characters including path separator characters `/`


| Name    | Type   | Default | Description                    |
|---------|--------|---------|--------------------------------|
| files   | [File] |         | Array of files to be filtered. |
| pattern | String |         | glob pattern                   |

Returns __[File]__ matching glob pattern.

### examples

Takes all files (recursively) from "pictures" directory 
and filters only those that end with "jpg" extension.

```
[File] jpgFiles = files("pictures") > filterFiles("**.jpg");
```
