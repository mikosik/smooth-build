## A fold<A, E>([E] array, A initial, (A, E)->A folder)

Returns a value created by applying `folder` function to each element of `array`, starting with `initial` value.

| Name     | Type      | Default | Description                                                                                |
|----------|-----------|---------|--------------------------------------------------------------------------------------------|
| array    | [E]       |         | Array with elements to fold.                                                               |
| initial  | A         |         | Initial value for the accumulator.                                                         |
| folder   | (A, E)->A |         | Function that takes the current accumulator value and an element, and returns a new value. |

Returns a value created by applying `folder` function to each element of `array`, starting with `initial` value.
