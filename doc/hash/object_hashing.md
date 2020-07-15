
Instances are stored in objects db as Merkle trees.
They are indexed by their hash.
Tree root has two children (except instance of `Type` type).
First child of the root represents an instance of type of instance represented by that root.
Second child depends on the type of of instance represented by root.

Type type
---------

```
typeTypeHash =
hash(
  hash(
    hash(0x00)
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
    hash(0x01)
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
    hash(0x05)
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
    hash(0x06)
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
    hash(0x04)
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
    hash(0x03),
    elementTypeHash
  )
)
```

Tuple type
-----------

```
structTypeHash =
hash(
  typeTypeHash,
  hash(
    hash(0x02),
    hash(
      field1TypeHash
      field2TypeHash
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
  hash(0x01 byte for true values, 0x00 for false values)
)
```

String instance
---------------

```
stringInstanceHash =
hash(
  stringTypeHash,
  hash(bytes of string encoded in UTF-8)
)
```

Blob instance
-------------

```
blobInstanceHash =
hash(
  blobTypeHash,
  hash(blob bytes)
)
```

Array instance
--------------

```
arrayInstanceHash =
hash(
  arrayTypeHash,
  hash(
    element1ValueHash,
    element2ValueHash,
    ...
    elementNValueHash
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
    field1ValueHash,
    field2ValueHash,
    ...
    fieldNValueHash
  )
)
```
