## A error<A>(String message)

Fails build with given error message.

| Name    | Type   | Default | Description                   |
|---------|--------|---------|-------------------------------|
| message | String |         | Error message to be reported. |

Function return type is __A__ so it can be assigned to any value however its invocation never
completes normally as it always fails with error so nothing is ever returned by this function in 
runtime.

### examples

```
Int result = error("Something bad happened!");
```
