## [A] filter<A>([A] array, (A)->Bool predicate)

Filters array using predicate.


| Name      | Type      | Default | Description                                                           |
|-----------|-----------|---------|-----------------------------------------------------------------------|
| array     | [A]       |         | Array of files to be filtered.                                        |
| predicate | (A)->Bool |         | Predicate that decides whether element should be kept in result list. |

Returns __[A]__ of elements from array that match predicate.

