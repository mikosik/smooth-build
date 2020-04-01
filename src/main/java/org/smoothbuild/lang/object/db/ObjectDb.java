package org.smoothbuild.lang.object.db;

import static java.util.Objects.requireNonNullElse;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.lang.object.db.Helpers.wrapException;
import static org.smoothbuild.lang.object.type.TypeNames.BLOB;
import static org.smoothbuild.lang.object.type.TypeNames.BOOL;
import static org.smoothbuild.lang.object.type.TypeNames.NOTHING;
import static org.smoothbuild.lang.object.type.TypeNames.STRING;
import static org.smoothbuild.lang.object.type.TypeNames.TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.ArrayBuilder;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.BlobBuilder;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.base.MerkleRoot;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.base.StructBuilder;
import org.smoothbuild.lang.object.type.BlobType;
import org.smoothbuild.lang.object.type.BoolType;
import org.smoothbuild.lang.object.type.ConcreteArrayType;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.object.type.NothingType;
import org.smoothbuild.lang.object.type.StringType;
import org.smoothbuild.lang.object.type.StructType;
import org.smoothbuild.lang.object.type.TypeType;

/**
 * This class is thread-safe.
 */
public class ObjectDb {
  private final HashedDb hashedDb;
  private final ConcurrentHashMap<Hash, ConcreteType> typeCache;

  /**
   * Following fields are effectively immutable - they are set only once in {@link #initialize()}
   * which is invoked before instance of ObjectDb is returned from factory method.
   */

  private BoolType boolType;
  private BlobType blobType;
  private NothingType nothingType;
  private StringType stringType;
  private TypeType typeType;

  public static ObjectDb objectDb(HashedDb hashedDb) {
      ObjectDb objectDb = new ObjectDb(hashedDb);
      objectDb.initialize();
      return objectDb;
  }

  private ObjectDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.typeCache = new ConcurrentHashMap<>();
  }

  private void initialize() {
    try {
      this.typeType = new TypeType(writeTypeTypeRoot(), hashedDb, this);
      this.blobType = new BlobType(writeBasicTypeRoot(typeType, BLOB), hashedDb, this);
      this.boolType = new BoolType(writeBasicTypeRoot(typeType, BOOL), hashedDb, this);
      this.nothingType = new NothingType(writeBasicTypeRoot(typeType, NOTHING), hashedDb, this);
      this.stringType = new StringType(writeBasicTypeRoot(typeType, STRING), hashedDb, this);

      cacheType(typeType);
      cacheType(blobType);
      cacheType(boolType);
      cacheType(nothingType);
      cacheType(stringType);
    } catch (HashedDbException e) {
      throw new ObjectDbException(e);
    }
  }

  // methods for creating non-type SObjects or its builders

  public ArrayBuilder arrayBuilder(ConcreteType elementType) {
    return new ArrayBuilder(arrayType(elementType), this);
  }

  public BlobBuilder blobBuilder() {
    return wrapException(() -> new BlobBuilder(this, hashedDb.sink()));
  }

  public Bool bool(boolean value) {
    return wrapException(() -> newBool(value));
  }

  public SString string(String string) {
    return wrapException(() -> newString(string));
  }

  public StructBuilder structBuilder(StructType type) {
    return new StructBuilder(type, this);
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
        Hash dataHash = hashes.get(1);
        return type.newObject(new MerkleRoot(hash, type, dataHash));
      }
    } catch (HashedDbException e) {
      throw new ObjectDbException(hash, e);
    }
  }

  // methods for returning type SObjects

  public ConcreteArrayType arrayType(ConcreteType elementType) {
    return cacheType(wrapException(() -> newArrayType(elementType)));
  }

  public BlobType blobType() {
    return blobType;
  }

  public BoolType boolType() {
    return boolType;
  }

  public NothingType nothingType() {
    return nothingType;
  }

  public StringType stringType() {
    return stringType;
  }

  public StructType structType(String name, Iterable<Field> fields) {
    return cacheType(wrapException(() -> newStructType(name, fields)));
  }

  public TypeType typeType() {
    return typeType;
  }

  private ConcreteType getTypeOrWrapException(Hash typeHash, Hash parentHash) {
    try {
      return getType(typeHash);
    } catch (ObjectDbException e) {
      throw new ObjectDbException(parentHash, e);
    }
  }

  public ConcreteType getType(MerkleRoot merkleRoot) {
    ConcreteType type = typeCache.get(merkleRoot.hash());
    if (type != null) {
      return type;
    } else {
      return readType(merkleRoot.hash(), merkleRoot.dataHash());
    }
  }

  private ConcreteType getType(Hash hash) {
    ConcreteType type = typeCache.get(hash);
    if (type != null) {
      return type;
    } else {
      try {
        List<Hash> hashes = hashedDb.readHashes(hash, 1, 2);
        if (hashes.size() == 1) {
          if (!typeType.hash().equals(hash)) {
            throw new ObjectDbException(hash, "Expected object which is instance of 'Type' type "
                + "but its Merkle tree has only one child (so it should be 'Type' type) but "
                + "it has different hash.");
          }
          return typeType;
        } else {
          Hash typeHash = hashes.get(0);
          if (!typeType.hash().equals(typeHash)) {
            throw new ObjectDbException(hash, "Expected object which is instance of 'Type' " +
                "type but its Merkle tree's first child is not 'Type' type.");
          }
          Hash dataHash = hashes.get(1);
          return readType(hash, dataHash);
        }
      } catch (HashedDbException e) {
        throw new ObjectDbException(hash, e);
      }
    }
  }

  private ConcreteType readType(Hash hash, Hash dataHash) {
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
          return cacheType(newArrayType(elementType, dataHash));
        default:
          assertSize(hash, name, hashes, 2);
          Iterable<Field> fields = readFieldSpecs(hashes.get(1), hash);
          return cacheType(newStructType(name, fields, dataHash));
      }
    } catch (HashedDbException e) {
      throw new ObjectDbException(hash, e);
    }
  }

  private static void assertSize(Hash hash, String typeName, List<Hash> hashes,
      int expectedSize) {
    if (hashes.size() != expectedSize) {
      throw new ObjectDbException(hash,
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
    @SuppressWarnings("unchecked")
    T result = (T) requireNonNullElse(typeCache.putIfAbsent(type.hash(), type), type);
    return result;
  }

  // methods for creating type's SObjects

  public Array newArray(ConcreteArrayType type, List<SObject> elements) throws
      HashedDbException {
    return type.newObject(writeRoot(type, writeArrayData(elements)));
  }

  public Blob newBlob(Hash dataHash) throws HashedDbException {
    return blobType.newObject(writeRoot(blobType, dataHash));
  }

  private Bool newBool(boolean value) throws HashedDbException {
    return boolType.newObject(writeRoot(boolType, writeBoolData(value)));
  }

  private SString newString(String string) throws HashedDbException {
    return stringType.newObject(writeRoot(stringType, writeStringData(string)));
  }

  public Struct newStruct(StructType type, List<SObject> objects) throws HashedDbException {
    return type.newObject(writeRoot(type, writeStructData(objects)));
  }

  private ConcreteArrayType newArrayType(ConcreteType elementType) throws
      HashedDbException {
    Hash dataHash = writeArrayTypeData(elementType);
    return newArrayType(elementType, dataHash);
  }

  private ConcreteArrayType newArrayType(ConcreteType elementType, Hash dataHash) throws
      HashedDbException {
    ConcreteType elementSuperType = elementType.superType();
    ConcreteArrayType superType =
        elementSuperType == null ? null : cacheType(newArrayType(elementSuperType));
    return new ConcreteArrayType(
        writeRoot(typeType, dataHash), superType, elementType, hashedDb, this);
  }

  private StructType newStructType(String name, Iterable<Field> fields) throws HashedDbException {
    Hash dataHash = writeStructTypeData(name, fields);
    return newStructType(name, fields, dataHash);
  }

  private StructType newStructType(String name, Iterable<Field> fields, Hash dataHash) throws
      HashedDbException {
    return new StructType(writeRoot(typeType, dataHash), name, fields, hashedDb, this);
  }

  // methods for writing Merkle node(s) to HashedDb

  private MerkleRoot writeBasicTypeRoot(TypeType typeType, String typeName) throws
      HashedDbException {
    return writeRoot(typeType, writeBasicTypeData(typeName));
  }

  private MerkleRoot writeTypeTypeRoot() throws HashedDbException {
    Hash dataHash = writeBasicTypeData(TYPE);
    Hash hash = hashedDb.writeHashes(dataHash);
    return new MerkleRoot(hash, null, dataHash);
  }

  private MerkleRoot writeRoot(ConcreteType type, Hash dataHash) throws HashedDbException {
    Hash hash = hashedDb.writeHashes(type.hash(), dataHash);
    return new MerkleRoot(hash, type, dataHash);
  }

  private Hash writeArrayData(List<SObject> elements) throws HashedDbException {
    return writeSequence(elements);
  }

  private Hash writeBoolData(boolean value) throws HashedDbException {
    return hashedDb.writeBoolean(value);
  }

  private Hash writeStringData(String string) throws HashedDbException {
    return hashedDb.writeString(string);
  }

  private Hash writeStructData(List<SObject> fieldValues) throws HashedDbException {
    return writeSequence(fieldValues);
  }

  private Hash writeSequence(List<SObject> objects) throws HashedDbException {
    Hash[] hashes = objects.stream()
        .map(SObject::hash)
        .toArray(Hash[]::new);
    return hashedDb.writeHashes(hashes);
  }

  private Hash writeArrayTypeData(ConcreteType elementType) throws HashedDbException {
    return hashedDb.writeHashes(hashedDb.writeString(""), elementType.hash());
  }

  private Hash writeBasicTypeData(String name) throws HashedDbException {
    return hashedDb.writeHashes(hashedDb.writeString(name));
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
}
