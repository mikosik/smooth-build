## [{A,B}] zip<A,B>([A] first, [B] second)

Combines two arrays into a single array of tuples, where each tuple contains elements from both arrays at the same position.

| Name   | Type | Default | Description                                |
|--------|------|---------|--------------------------------------------|
| first  | [A]  |         | First array to be zipped.                  |
| second | [B]  |         | Second array to be zipped.                 |

Returns __[{A,B}]__ array of tuples where each tuple contains elements from both arrays at the same position. The length of the result array is equal to the minimum length of the input arrays.

### examples

Zipping two arrays of different types:
```
[{String,Int}] zipped = zip(["a", "b", "c"], [1, 2, 3]);
// Result: [{"a", 1}, {"b", 2}, {"c", 3}]
```

Zipping arrays of different lengths (the shorter array determines the result length):
```
[{String,Int}] zipped = zip(["a", "b"], [1, 2, 3, 4]);
// Result: [{"a", 1}, {"b", 2}]
```
