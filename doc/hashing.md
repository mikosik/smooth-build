
Instances are kept in HashedDb as Merkle trees.
Tree root has two children (except instance of `Type` type).
First children of the root is an instance of type of instance represented by root.
Second children depends on the type of of instance represented by root.

Type type
---------

```
typeTypeHash =
hash(
  hash(
    hash("Type")
  )
)
```

Nothing type
---------

```
nothingTypeHash =
hash(
  typeTypeHash,
  hash(
    hash("Nothing")
  )
)
```

Bool type
---------

```
boolTypeHash =
hash(
  typeTypeHash,
  hash(
    hash("Bool")
  )
)
```

String type
-----------

```
stringTypeHash =
hash(
  typeTypeHash,
  hash(
    hash("String")
  )
)
```

Blob type
---------

```
blobTypeHash =
hash(
  typeTypeHash,
  hash(
    hash("Blob")
  )
)
```

Array type
----------

```
arrayTypeHash =
hash(
  typeTypeHash,
  hash(
    hash(""),
    elementTypeHash
  )
)
```

Struct type
-----------

```
structTypeHash =
hash(
  typeTypeHash,
  hash(
    hash(structName),
    hash(
      hash(
        hash(field1Name),
        field1TypeHash
      ),
      hash(
        hash(field2Name),
        field2TypeHash
      ),
    )
  )
)
```

Bool instance
---------------

```
boolInstanceHash =
hash(
  boolTypeHash,
  hash(boolValue)
)
```

String instance
---------------

```
stringInstanceHash =
hash(
  TypeHash,
  hash(string)
)
```

Blob instance
-------------

```
blobInstanceHash =
hash(
  blobTypeHash,
  hash(string)
)
```

Array instance
--------------

```
arrayInstanceHash =
hash(
  arrayTypeHash,
  hash(
    element1Hash,
    element2Hash,
    ...
    elementNHash
  )
)
```

Struct instance
---------------

```
structInstanceHash =
hash(
  structTypeHash,
  hash(
    field1Hash,
    field2Hash,
    ...
    fieldNHash
  )
)
```
