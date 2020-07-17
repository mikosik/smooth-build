package org.smoothbuild.record.db;

import static com.google.common.collect.Streams.stream;
import static java.util.Objects.requireNonNullElse;
import static org.smoothbuild.record.type.TypeKind.ARRAY;
import static org.smoothbuild.record.type.TypeKind.BLOB;
import static org.smoothbuild.record.type.TypeKind.BOOL;
import static org.smoothbuild.record.type.TypeKind.NOTHING;
import static org.smoothbuild.record.type.TypeKind.STRING;
import static org.smoothbuild.record.type.TypeKind.TUPLE;
import static org.smoothbuild.record.type.TypeKind.TYPE;
import static org.smoothbuild.record.type.TypeKind.typeKindMarkedWith;
import static org.smoothbuild.util.Iterables.map;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.ArrayBuilder;
import org.smoothbuild.record.base.Blob;
import org.smoothbuild.record.base.BlobBuilder;
import org.smoothbuild.record.base.Bool;
import org.smoothbuild.record.base.MerkleRoot;
import org.smoothbuild.record.base.SObject;
import org.smoothbuild.record.base.SString;
import org.smoothbuild.record.base.Tuple;
import org.smoothbuild.record.type.ArrayType;
import org.smoothbuild.record.type.BinaryType;
import org.smoothbuild.record.type.BlobType;
import org.smoothbuild.record.type.BoolType;
import org.smoothbuild.record.type.NothingType;
import org.smoothbuild.record.type.StringType;
import org.smoothbuild.record.type.TupleType;
import org.smoothbuild.record.type.TypeKind;
import org.smoothbuild.record.type.TypeType;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class ObjectDb {
  private final HashedDb hashedDb;
  private final ConcurrentHashMap<Hash, BinaryType> typeCache;

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

  public ArrayBuilder arrayBuilder(BinaryType elementType) {
    return new ArrayBuilder(arrayType(elementType), this);
  }

  public BlobBuilder blobBuilder() {
    return Helpers.wrapException(() -> new BlobBuilder(this, hashedDb.sink()));
  }

  public Bool bool(boolean value) {
    return Helpers.wrapException(() -> newBool(value));
  }

  public SString string(String string) {
    return Helpers.wrapException(() -> newString(string));
  }

  public Tuple struct(TupleType tupleType, Iterable<? extends SObject> fields) {
    List<SObject> fieldList = ImmutableList.copyOf(fields);
    var types = tupleType.elementTypes();
    if (types.size() != fieldList.size()) {
      throw new IllegalArgumentException("Type specifies " + types.size() +
          " fields but provided " + fieldList.size() + ".");
    }
    for (int i = 0; i < types.size(); i++) {
      BinaryType specifiedType = types.get(i);
      BinaryType fieldType = fieldList.get(i).type();
      if (!specifiedType.equals(fieldType)) {
        throw new IllegalArgumentException("Type (Struct) specifies field at index " + i
            + " with type " + specifiedType + " but provided field has type " + fieldType
            + " at that index.");
      }
    }
    return Helpers.wrapException(() ->newStruct(tupleType, fieldList));
  }

  public SObject get(Hash hash) {
    try {
      List<Hash> hashes = hashedDb.readHashes(hash, 1, 2);
      if (hashes.size() == 1) {
          // If Merkle tree root has only one child then it must
          // be Type("Type") smooth object. getType() will verify it.
          return getType(hash);
      } else {
        BinaryType type = getTypeOrWrapException(hashes.get(0), hash);
        Hash dataHash = hashes.get(1);
        return type.newJObject(new MerkleRoot(hash, type, dataHash));
      }
    } catch (HashedDbException e) {
      throw new ObjectDbException(hash, e);
    }
  }

  // methods for returning type SObjects

  public ArrayType arrayType(BinaryType elementType) {
    return cacheType(Helpers.wrapException(() -> newArrayType(elementType)));
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

  public TupleType structType(Iterable<? extends BinaryType> fieldTypes) {
    return cacheType(Helpers.wrapException(() -> newStructType(fieldTypes)));
  }

  public TypeType typeType() {
    return typeType;
  }

  private BinaryType getTypeOrWrapException(Hash typeHash, Hash parentHash) {
    try {
      return getType(typeHash);
    } catch (ObjectDbException e) {
      throw new ObjectDbException(parentHash, e);
    }
  }

  public BinaryType getType(MerkleRoot merkleRoot) {
    BinaryType type = typeCache.get(merkleRoot.hash());
    if (type != null) {
      return type;
    } else {
      return readType(merkleRoot.hash(), merkleRoot.dataHash());
    }
  }

  private BinaryType getType(Hash hash) {
    BinaryType type = typeCache.get(hash);
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

  private BinaryType readType(Hash hash, Hash dataHash) {
    try {
      List<Hash> hashes = hashedDb.readHashes(dataHash, 1, 2);
      byte marker = hashedDb.readByte(hashes.get(0));
      TypeKind typeKind = typeKindMarkedWith(marker);
      if (typeKind == null) {
        throw new ObjectDbException(hash,
            "It is instance of type but it has illegal TypeKind marker = " + marker + ".");
      }
      return switch (typeKind) {
        case TYPE -> null;
        case BOOL -> {
          assertSize(hash, BOOL, hashes, 1);
          yield boolType;
        }
        case STRING -> {
          assertSize(hash, STRING, hashes, 1);
          yield stringType;
        }
        case BLOB -> {
          assertSize(hash, BLOB, hashes, 1);
          yield blobType;
        }
        case NOTHING -> {
          assertSize(hash, NOTHING, hashes, 1);
          yield nothingType;
        }
        case ARRAY -> {
          assertSize(hash, ARRAY, hashes, 2);
          BinaryType elementType = getTypeOrWrapException(hashes.get(1), hash);
          yield cacheType(newArrayType(elementType, dataHash));
        }
        case TUPLE -> {
          assertSize(hash, TUPLE, hashes, 2);
          ImmutableList<BinaryType> fields = readStructTypeFieldTypes(hashes.get(1), hash);
          yield cacheType(newStructType(fields, dataHash));
        }
      };
    } catch (HashedDbException e) {
      throw new ObjectDbException(hash, e);
    }
  }

  private static void assertSize(Hash hash, TypeKind typeKind, List<Hash> hashes,
      int expectedSize) {
    if (hashes.size() != expectedSize) {
      throw new ObjectDbException(hash,
          "It is " + typeKind + " type but its Merkle root has " + hashes.size() +
          " children when " + expectedSize + " is expected.");
    }
  }

  private ImmutableList<BinaryType> readStructTypeFieldTypes(Hash hash, Hash parentHash) {
    var builder = ImmutableList.<BinaryType>builder();
    List<Hash> fieldTypeHashes = readStructTypeFieldTypeHashes(hash, parentHash);
    for (int i = 0; i < fieldTypeHashes.size(); i++) {
      try {
        builder.add(getType(fieldTypeHashes.get(i)));
      } catch (ObjectDbException e) {
        throw new ObjectDbException(parentHash, "It is a Struct Type and reading field type " +
            "at index " + i + " caused error.", e);
      }
    }
    return builder.build();
  }

  private List<Hash> readStructTypeFieldTypeHashes(Hash hash, Hash parentHash) {
    try {
      return hashedDb.readHashes(hash);
    } catch (HashedDbException e) {
      throw new ObjectDbException(
          parentHash, "It is a Struct Type and reading its field types array caused error.", e);
    }
  }

  private <T extends BinaryType> T cacheType(T type) {
    @SuppressWarnings("unchecked")
    T result = (T) requireNonNullElse(typeCache.putIfAbsent(type.hash(), type), type);
    return result;
  }

  // methods for creating type's SObjects

  public Array newArray(ArrayType type, Iterable<? extends SObject> elements)
      throws HashedDbException {
    return type.newJObject(writeRoot(type, writeArrayData(elements)));
  }

  public Blob newBlob(Hash dataHash) throws HashedDbException {
    return blobType.newJObject(writeRoot(blobType, dataHash));
  }

  private Bool newBool(boolean value) throws HashedDbException {
    return boolType.newJObject(writeRoot(boolType, writeBoolData(value)));
  }

  private SString newString(String string) throws HashedDbException {
    return stringType.newJObject(writeRoot(stringType, writeStringData(string)));
  }

  private Tuple newStruct(TupleType type, List<?extends SObject> objects) throws HashedDbException {
    return type.newJObject(writeRoot(type, writeStructData(objects)));
  }

  private ArrayType newArrayType(BinaryType elementType) throws
      HashedDbException {
    Hash dataHash = writeArrayTypeData(elementType);
    return newArrayType(elementType, dataHash);
  }

  private ArrayType newArrayType(BinaryType elementType, Hash dataHash) throws
      HashedDbException {
    return new ArrayType(writeRoot(typeType, dataHash), elementType, hashedDb, this);
  }

  private TupleType newStructType(Iterable<? extends BinaryType> fieldTypes)
      throws HashedDbException {
    Hash dataHash = writeStructTypeData(fieldTypes);
    return newStructType(fieldTypes, dataHash);
  }

  private TupleType newStructType(Iterable<? extends BinaryType> fieldTypes,
      Hash dataHash) throws HashedDbException {
    return new TupleType(writeRoot(typeType, dataHash), fieldTypes, hashedDb, this);
  }

  // methods for writing Merkle node(s) to HashedDb

  private MerkleRoot writeBasicTypeRoot(TypeType typeType, TypeKind typeKind) throws
      HashedDbException {
    return writeRoot(typeType, writeBasicTypeData(typeKind));
  }

  private MerkleRoot writeTypeTypeRoot() throws HashedDbException {
    Hash dataHash = writeBasicTypeData(TYPE);
    Hash hash = hashedDb.writeHashes(dataHash);
    return new MerkleRoot(hash, null, dataHash);
  }

  private MerkleRoot writeRoot(BinaryType type, Hash dataHash) throws HashedDbException {
    Hash hash = hashedDb.writeHashes(type.hash(), dataHash);
    return new MerkleRoot(hash, type, dataHash);
  }

  private Hash writeArrayData(Iterable<? extends SObject> elements) throws HashedDbException {
    return writeSequence(elements);
  }

  private Hash writeBoolData(boolean value) throws HashedDbException {
    return hashedDb.writeBoolean(value);
  }

  private Hash writeStringData(String string) throws HashedDbException {
    return hashedDb.writeString(string);
  }

  private Hash writeStructData(List<? extends SObject> fieldValues) throws HashedDbException {
    return writeSequence(fieldValues);
  }

  private Hash writeSequence(Iterable<? extends SObject> objects) throws HashedDbException {
    Hash[] hashes = stream(objects)
        .map(SObject::hash)
        .toArray(Hash[]::new);
    return hashedDb.writeHashes(hashes);
  }

  private Hash writeArrayTypeData(BinaryType elementType) throws HashedDbException {
    return writeNonBasicTypeData(ARRAY, elementType.hash());
  }

  private Hash writeStructTypeData(Iterable<? extends BinaryType> fieldTypes)
      throws HashedDbException {
    Hash fields = hashedDb.writeHashes(map(fieldTypes, BinaryType::hash));
    return writeNonBasicTypeData(TUPLE, fields);
  }

  private Hash writeNonBasicTypeData(TypeKind typeKind, Hash fields) throws HashedDbException {
    return hashedDb.writeHashes(hashedDb.writeByte(typeKind.marker()), fields);
  }

  private Hash writeBasicTypeData(TypeKind typeKind) throws HashedDbException {
    return hashedDb.writeHashes(hashedDb.writeByte(typeKind.marker()));
  }
}
