Task is most atomic operation that can be executed by smooth build internally.
Task is a computation done one some Input which produces some Output.
Input is a sequence of smooth objects.
Output is a single smooth object plus sequence of messages (errors, warnings, infos). 
After each Task execution:
 - its Output is stored in objects db.
 - hash of its Output is stored in results db indexed by Task hash
 

Task
----

```
taskHash =
hash(
  runtimeHash,
  computationHash,
  inputHash,
)
```

runtime
-------

```
runtimeHash =
hash(
  javaPlatformHash,
  smoothReleaseJarHash,
)
```

where `smoothReleaseJarHash` is hash of smooth build release jar.


javaPlatform
------------

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

Object Computation
-----------------

```
objectComputationHash =
hash(
  hash(0x00),
  hash of smooth object,
)
```

Array computation
-----------------

```
arrayComputationHash =
hash(
  hash(0x01),
)
```

Identity computation
--------------------

```
identityComputationHash =
hash(
  hash(0x02),
)
```

Native Call Computation
-----------------------

```
nativeCallComputation =
hash(
  hash(0x03),
  nativeFunctionHash,
)
```

Native Function
---------------

```
nativeFunctionHash =
hash(
  jarContainingFunctionImplementationHash,
  hash(functionName),
)
```

Convert Computation
-------------------

```
convertComputationHash =
hash(
  hash(0x04),
  destinationTypeHash,
)
```

Constructor Call Computation
----------------------------

```
constructorCallComputationHash =
hash(
  hash(0x05),
  constructedInstanceTypeHash,
)
```

Accessor Call Computation
-------------------------

```
accessorCallComputationHash =
hash(
  hash(0x06),
  hash(fieldName),
)
```

Input
-----

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