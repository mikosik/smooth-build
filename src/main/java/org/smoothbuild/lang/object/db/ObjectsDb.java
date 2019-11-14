package org.smoothbuild.lang.object.db;

import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.lang.object.type.TypeNames.BLOB;
import static org.smoothbuild.lang.object.type.TypeNames.BOOL;
import static org.smoothbuild.lang.object.type.TypeNames.NOTHING;
import static org.smoothbuild.lang.object.type.TypeNames.STRING;
import static org.smoothbuild.lang.object.type.TypeNames.TYPE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.object.base.ArrayBuilder;
import org.smoothbuild.lang.object.base.BlobBuilder;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.StructBuilder;
import org.smoothbuild.lang.object.type.BlobType;
import org.smoothbuild.lang.object.type.BoolType;
import org.smoothbuild.lang.object.type.ConcreteArrayType;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.object.type.NothingType;
import org.smoothbuild.lang.object.type.StringType;
import org.smoothbuild.lang.object.type.StructType;
import org.smoothbuild.lang.object.type.TypeType;

public class ObjectsDb {
  private final HashedDb hashedDb;

  private final Map<Hash, ConcreteType> typesCache;
  private TypeType typeType;
  private BoolType boolType;
  private StringType stringType;
  private BlobType blobType;
  private NothingType nothingType;

  public static ObjectsDb objectsDb(HashedDb hashedDb) {
      ObjectsDb objectsDb = new ObjectsDb(hashedDb);
      objectsDb.initialize();
      return objectsDb;
  }

  private ObjectsDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.typesCache = new HashMap<>();
  }

  private void initialize() {
    try {
      this.typeType = new TypeType(writeBasicTypeData(TYPE), this, hashedDb);
      this.boolType = new BoolType(writeBasicTypeData(BOOL), typeType, hashedDb, this);
      this.stringType = new StringType(writeBasicTypeData(STRING), typeType, hashedDb, this);
      this.blobType = new BlobType(writeBasicTypeData(BLOB), typeType, hashedDb, this);
      this.nothingType = new NothingType(writeBasicTypeData(NOTHING), typeType, hashedDb, this);

      typesCache.put(typeType.hash(), typeType);
      typesCache.put(boolType.hash(), boolType);
      typesCache.put(stringType.hash(), stringType);
      typesCache.put(blobType.hash(), blobType);
      typesCache.put(nothingType.hash(), nothingType);
    } catch (HashedDbException e) {
      throw new ObjectsDbException(e);
    }
  }

  // methods for creating non-type SObjects or its builders

  public ArrayBuilder arrayBuilder(ConcreteType elementType) {
    return new ArrayBuilder(arrayType(elementType), this);
  }

  public StructBuilder structBuilder(StructType type) {
    return new StructBuilder(type, this);
  }

  public BlobBuilder blobBuilder() {
    return wrapException(() -> new BlobBuilder(blobType, hashedDb));
  }

  public SString string(String string) {
      return wrapException(() -> new SString(writeStringData(string), stringType, hashedDb));
  }

  public Bool bool(boolean value) {
    return wrapException(() -> new Bool(writeBoolData(value), boolType, hashedDb));
  }

  public SObject get(Hash hash) {
    try {
      List<Hash> hashes = hashedDb.readHashes(hash, 1, 2);
      if (hashes.size() == 1) {
          // If Merkle tree root has only one child then it must
          // be Type("Type") smooth object. getType() will verify it.
          return getType(hash);
      } else {
        ConcreteType type = getTypeOrWrapException(hashes.get(0), hash);
        if (type.equals(typeType)) {
            return getType(hash);
          } else {
            return type.newSObject(hashes.get(1));
          }
      }
    } catch (HashedDbException e) {
      throw new ObjectsDbException(hash, e);
    }
  }


  // methods for returning type SObjects
  public TypeType typeType() {
    return typeType;
  }

  public BoolType boolType() {
    return boolType;
  }

  public StringType stringType() {
    return stringType;
  }

  public BlobType blobType() {
    return blobType;
  }

  public NothingType nothingType() {
    return nothingType;
  }

  public ConcreteArrayType arrayType(ConcreteType elementType) {
    return cacheType(wrapException(() -> newArrayTypeSObject(elementType)));
  }

  public StructType structType(String name, Iterable<Field> fields) {
    return cacheType(wrapException(() -> newStructTypeSObject(name, fields)));
  }

  private ConcreteType getTypeOrWrapException(Hash typeHash, Hash parentHash) {
    try {
      return getType(typeHash);
    } catch (ObjectsDbException e) {
      throw new ObjectsDbException(parentHash, e);
    }
  }

  private ConcreteType getType(Hash hash) {
    if (typesCache.containsKey(hash)) {
      return typesCache.get(hash);
    } else {
      try {
        List<Hash> hashes = hashedDb.readHashes(hash, 1, 2);
        if (hashes.size() == 1) {
          if (!typeType.hash().equals(hash)) {
            throw new ObjectsDbException(hash, "Expected object which is instance of 'Type' type "
                + "but its Merkle tree has only one child (so it should be 'Type' type) but "
                + "it has different hash.");
          }
          return typeType;
        } else {
          Hash typeHash = hashes.get(0);
          if (!typeType.hash().equals(typeHash)) {
            throw new ObjectsDbException(hash, "Expected object which is instance of 'Type' " +
                "type but its Merkle tree's first child is not 'Type' type.");
          }
          Hash dataHash = hashes.get(1);
          return readType(hash, dataHash);
        }
      } catch (HashedDbException e) {
        throw new ObjectsDbException(hash, e);
      }
    }
  }

  public ConcreteType readType(Hash hash, Hash dataHash) {
    try {
      List<Hash> hashes = hashedDb.readHashes(dataHash, 1, 2);
      String name = hashedDb.readString(hashes.get(0));
      switch (name) {
        case BOOL:
          assertSize(hash, name, hashes, 1);
          return boolType;
        case STRING:
          assertSize(hash, name, hashes, 1);
          return stringType;
        case BLOB:
          assertSize(hash, name, hashes, 1);
          return blobType;
        case NOTHING:
          assertSize(hash, name, hashes, 1);
          return nothingType;
        case "":
          assertSize(hash, "[]", hashes, 2);
          ConcreteType elementType = getTypeOrWrapException(hashes.get(1), hash);
          return cacheType(newArrayTypeSObject(elementType, dataHash));
        default:
          assertSize(hash, name, hashes, 2);
          Iterable<Field> fields = readFieldSpecs(hashes.get(1), hash);
          return cacheType(newStructTypeSObject(name, fields, dataHash));
      }
    } catch (HashedDbException e) {
      throw new ObjectsDbException(hash, e);
    }
  }

  private static void assertSize(Hash hash, String typeName, List<Hash> hashes,
      int expectedSize) {
    if (hashes.size() != expectedSize) {
      throw new ObjectsDbException(hash,
          "It is '" + typeName + "' type but its Merkle root has " + hashes.size() +
          " children when " + expectedSize + " is expected.");
    }
  }

  private Iterable<Field> readFieldSpecs(Hash hash, Hash parentHash) throws HashedDbException {
    List<Field> result = new ArrayList<>();
    for (Hash fieldHash : hashedDb.readHashes(hash)) {
      result.add(getFieldSpec(fieldHash, parentHash));
    }
    return result;
  }

  private Field getFieldSpec(Hash fieldHash, Hash parentHash) throws HashedDbException {
    List<Hash> hashes = hashedDb.readHashes(fieldHash, 2);
    Hash nameHash = hashes.get(0);
    Hash typeHash = hashes.get(1);
    String name = hashedDb.readString(nameHash);
    ConcreteType type = getTypeOrWrapException(typeHash, parentHash);
    return new Field(type, name, unknownLocation());
  }

  private <T extends ConcreteType> T cacheType(T type) {
    Hash hash = type.hash();
    if (typesCache.containsKey(hash)) {
      return (T) typesCache.get(hash);
    } else {
      typesCache.put(hash, type);
      return type;
    }
  }

  // methods for creating type's SObjects

  private ConcreteArrayType newArrayTypeSObject(ConcreteType elementType) throws
      HashedDbException {
    Hash dataHash = writeArrayTypeData(elementType);
    return newArrayTypeSObject(elementType, dataHash);
  }

  private ConcreteArrayType newArrayTypeSObject(ConcreteType elementType, Hash dataHash) throws
      HashedDbException {
    ConcreteType elementSuperType = elementType.superType();
    ConcreteArrayType superType =
        elementSuperType == null ? null : cacheType(newArrayTypeSObject(elementSuperType));
    return new ConcreteArrayType(dataHash, typeType, superType, elementType, hashedDb, this);
  }

  private StructType newStructTypeSObject(String name, Iterable<Field> fields) throws HashedDbException {
    Hash dataHash = writeStructTypeData(name, fields);
    return newStructTypeSObject(name, fields, dataHash);
  }

  private StructType newStructTypeSObject(String name, Iterable<Field> fields, Hash dataHash) {
    return new StructType(dataHash, typeType, name, fields, hashedDb, this);
  }

  // methods for writing SObject's data Merkle node(s) to HashedDb

  private Hash writeStringData(String string) throws HashedDbException {
    return hashedDb.writeString(string);
  }

  private Hash writeBoolData(boolean value) throws HashedDbException {
    return hashedDb.writeBoolean(value);
  }

  public Hash writeStructData(List<SObject> fieldValues) throws HashedDbException {
    return writeSequence(fieldValues);
  }

  public Hash writeArrayData(List<SObject> elements) throws HashedDbException {
    return writeSequence(elements);
  }

  private Hash writeSequence(List<SObject> objects) throws HashedDbException {
    Hash[] hashes = objects.stream()
        .map(SObject::hash)
        .toArray(Hash[]::new);
    return hashedDb.writeHashes(hashes);
  }

  private Hash writeStructTypeData(String name, Iterable<Field> fields) throws HashedDbException {
      return hashedDb.writeHashes(hashedDb.writeString(name), writeFieldSpecs(fields));
  }

  private Hash writeFieldSpecs(Iterable<Field> fieldSpecs) throws HashedDbException {
    List<Hash> fieldHashes = new ArrayList<>();
    for (Field field : fieldSpecs) {
      fieldHashes.add(writeFieldSpec(field));
    }
    return hashedDb.writeHashes(fieldHashes.toArray(new Hash[0]));
  }

  private Hash writeFieldSpec(Field fieldSpec) throws HashedDbException {
    return hashedDb.writeHashes(hashedDb.writeString(fieldSpec.name()), fieldSpec.type().hash());
  }

  private Hash writeArrayTypeData(ConcreteType elementType) throws HashedDbException {
    return hashedDb.writeHashes(hashedDb.writeString(""), elementType.hash());
  }

  private Hash writeBasicTypeData(String name) throws HashedDbException {
    return hashedDb.writeHashes(hashedDb.writeString(name));
  }

  @FunctionalInterface
  private static interface HashedDbCallable<T> {
    public T call() throws HashedDbException;
  }

  private static <T> T wrapException(HashedDbCallable<T> callable) {
    try {
      return callable.call();
    } catch (HashedDbException e) {
      throw new ObjectsDbException(e);
    }
  }
}
