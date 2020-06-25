Most atomic operation that can be executed by smooth build internally is called Computation.
It applies Algorithm to some Input and produces Output or fails with ComputationException.
Input is a sequence of smooth objects.
Output consists of
 - result (single smooth object) - present only when there's no error messages 
 - sequence of messages (errors, warnings, infos)

After each computation:
 - its Output's result (if present) is stored in objects db
 - its Output messages are stored as Message array in objects db
 - hash of Message array plus (optionally) hash of result are stored in outputs db indexed by 
 computation hash
 

### Computation


```
computationHash =
hash(
  sandboxHash,
  computationHash,
  inputHash,
)
```

### sandbox

```
sandboxHash =
hash(
  javaPlatformHash,
  smoothReleaseJarHash,
)
```

where `smoothReleaseJarHash` is hash of smooth build release jar.


### javaPlatform

```
javaPlatformHash =
hash(
  hash(value of system property "java.vendor"),
  hash(value of system property "java.version"),
  hash(value of system property "java.runtime.name"),
  hash(value of system property "java.runtime.version"),
  hash(value of system property "java.vm.name"),
  hash(value of system property "java.vm.version"),
)
```

### Object Computation

```
objectComputationHash =
hash(
  hash(0x00),
  hash of smooth object,
)
```

### Array computation

```
arrayComputationHash =
hash(
  hash(0x00),
)
```

### Native Call Computation

```
nativeCallComputation =
hash(
  hash(0x01),
  nativeFunctionHash,
)
```

### Native Function

```
nativeFunctionHash =
hash(
  jarContainingFunctionImplementationHash,
  hash(functionName),
)
```

### Convert Computation

```
convertComputationHash =
hash(
  hash(0x02),
  destinationTypeHash,
)
```

### Constructor Call Computation

```
constructorCallComputationHash =
hash(
  hash(0x03),
  constructedInstanceTypeHash,
)
```

### Accessor Call Computation

```
accessorCallComputationHash =
hash(
  hash(0x04),
  hash(fieldName),
)
```

### String Literal Computation

```
StringLiteralComputationHash =
hash(
  hash(0x05),
  hash(string),
)
```

### Input

```
inputHash =
hash(
  argument1,
  argument2,
  ....
  argumentN,
)
```
where arguments are smooth objects
