## if

Returns one of two values depending on bool condition.
Note that like in any function call in smooth, 
arguments are evaluated lazily, possibly not at all.
In case of `if` function only one of `then`, `else` arguments is evaluated
depending on value of `condition` arguments.

 | Name      | Type | Default | Description                                           |
 |-----------|------|---------|-------------------------------------------------------|
 | condition | Bool |         | Condition specifying which of two values is returned. |
 | then      | a    |         | Value that is returned when condition is true.        |
 | else      | a    |         | Value that is returned when condition is false.       |

Returns `thenValue` when `condition` is true, `elseValue` otherwise. 


