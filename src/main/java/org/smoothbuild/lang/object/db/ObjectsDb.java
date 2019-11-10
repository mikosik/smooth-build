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

import javax.inject.Inject;

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

  @Inject
  public ObjectsDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.typesCache = new HashMap<>();
  }

  public ArrayBuilder arrayBuilder(ConcreteType elementType) {
    return new ArrayBuilder(arrayType(elementType), hashedDb);
  }

  public StructBuilder structBuilder(StructType type) {
    return new StructBuilder(type, hashedDb);
  }

  public BlobBuilder blobBuilder() {
    try {
      return new BlobBuilder(blobType(), hashedDb);
    } catch (HashedDbException e) {
      throw new ObjectsDbException(e);
    }
  }

  public SString string(String string) {
    try {
      return new SString(hashedDb.writeString(string), stringType(), hashedDb);
    } catch (HashedDbException e) {
      throw new ObjectsDbException(e);
    }
  }

  public Bool bool(boolean value) {
    try {
      return new Bool(hashedDb.writeBoolean(value), boolType(), hashedDb);
    } catch (HashedDbException e) {
      throw new ObjectsDbException(e);
    }
  }

  public SObject get(Hash hash) {
    try {
      List<Hash> hashes = hashedDb.readHashes(hash, 1, 2);
      if (hashes.size() == 1) {
          // If Merkle tree root has only one child then it must
          // be Type("Type") smooth object. getType() will verify it.
          return getType(hash);
      } else {
          ConcreteType type = getType(hashes.get(0));
          if (type.equals(typeType())) {
            return getType(hash);
          } else {
            return type.newSObject(hashes.get(1));
          }
      }
    } catch (HashedDbException e) {
      throw new ObjectsDbException(hash, e);
    }
  }

  public TypeType typeType() {
    if (typeType == null) {
      typeType = new TypeType(writeBasicTypeData(TYPE), this, hashedDb);
      typesCache.put(typeType.hash(), typeType);
    }
    return typeType;
  }

  public BoolType boolType() {
    if (boolType == null) {
      boolType = new BoolType(writeBasicTypeData(BOOL), typeType(), hashedDb, this);
      typesCache.put(boolType.hash(), boolType);
    }
    return boolType;
  }

  public StringType stringType() {
    if (stringType == null) {
      stringType = new StringType(writeBasicTypeData(STRING), typeType(), hashedDb, this);
      typesCache.put(stringType.hash(), stringType);
    }
    return stringType;
  }

  public BlobType blobType() {
    if (blobType == null) {
      blobType = new BlobType(writeBasicTypeData(BLOB), typeType(), hashedDb, this);
      typesCache.put(blobType.hash(), blobType);
    }
    return blobType;
  }

  public NothingType nothingType() {
    if (nothingType == null) {
      nothingType = new NothingType(writeBasicTypeData(NOTHING), typeType(), hashedDb, this);
      typesCache.put(nothingType.hash(), nothingType);
    }
    return nothingType;
  }

  private Hash writeBasicTypeData(String name) {
    try {
      return hashedDb.writeHashes(hashedDb.writeString(name));
    } catch (HashedDbException e) {
      throw new ObjectsDbException(e);
    }
  }

  public ConcreteArrayType arrayType(ConcreteType elementType) {
    Hash dataHash = writeArray(elementType);
    ConcreteArrayType superType = possiblyNullArrayType(elementType.superType());
    return cacheType(
        new ConcreteArrayType(dataHash, typeType(), superType, elementType, hashedDb, this));
  }

  private Hash writeArray(ConcreteType elementType) {
    try {
      return hashedDb.writeHashes(hashedDb.writeString(""), elementType.hash());
    } catch (HashedDbException e) {
      throw new ObjectsDbException(e);
    }
  }

  private ConcreteArrayType possiblyNullArrayType(ConcreteType elementType) {
    return elementType == null ? null : arrayType(elementType);
  }

  public StructType structType(String name, Iterable<Field> fields) {
    Hash hash = writeStruct(name, fields);
    return cacheType(new StructType(hash, typeType(), name, fields, hashedDb, this));
  }

  private Hash writeStruct(String name, Iterable<Field> fields) {
    try {
      return hashedDb.writeHashes(hashedDb.writeString(name), writeFields(fields));
    } catch (HashedDbException e) {
      throw new ObjectsDbException(e);
    }
  }

  private Hash writeFields(Iterable<Field> fields) throws HashedDbException {
    List<Hash> fieldHashes = new ArrayList<>();
    for (Field field : fields) {
      fieldHashes.add(writeField(field.name(), field.type()));
    }
    return hashedDb.writeHashes(fieldHashes.toArray(new Hash[0]));
  }

  private Hash writeField(String name, ConcreteType type) throws HashedDbException {
    return hashedDb.writeHashes(hashedDb.writeString(name), type.hash());
  }

  private ConcreteType getType(Hash hash) {
    if (typesCache.containsKey(hash)) {
      return typesCache.get(hash);
    } else {
      return newTypeSObject(hash);
    }
  }

  private ConcreteType newTypeSObject(Hash hash) {
    try {
      List<Hash> hashes = hashedDb.readHashes(hash, 1, 2);
      if (hashes.size() == 1) {
        if (!typeType().hash().equals(hash)) {
          throw new ObjectsDbException(hash, "Expected object which is instance of 'Type' type "
              + "but its Merkle tree has only one child (so it should be 'Type' type) but "
              + "it has different hash.");
        }
        return typeType();
      } else {
        Hash typeHash = hashes.get(0);
        if (!typeType().hash().equals(typeHash)) {
          throw new ObjectsDbException(hash, "Expected object which is instance of 'Type' " +
              "type but its Merkle tree's first child is not 'Type' type.");
        }
        Hash dataHash = hashes.get(1);
        return readFromDataHash(dataHash, hash);
      }
    } catch (HashedDbException e) {
      // TODO calls from this class should catch it and properly wrap to
      // let user know why we needed to read this type
      throw new ObjectsDbException(hash, e);
    }
  }

  public ConcreteType readFromDataHash(Hash typeDataHash, Hash typeHash) {
    try {
      List<Hash> hashes = hashedDb.readHashes(typeDataHash, 1, 2);
      String name = hashedDb.readString(hashes.get(0));
      switch (name) {
        case BOOL:
          assertSize(typeHash, name, hashes, 1);
          return boolType();
        case STRING:
          assertSize(typeHash, name, hashes, 1);
          return stringType();
        case BLOB:
          assertSize(typeHash, name, hashes, 1);
          return blobType();
        case NOTHING:
          assertSize(typeHash, name, hashes, 1);
          return nothingType();
        case "":
          assertSize(typeHash, "[]", hashes, 2);
          ConcreteType elementType = getType(hashes.get(1));
          ConcreteArrayType superType = possiblyNullArrayType(elementType.superType());
          return cacheType(new ConcreteArrayType(
              typeDataHash, typeType(), superType, elementType, hashedDb, this));
        default:
          assertSize(typeHash, name, hashes, 2);
          Iterable<Field> fields = readFields(hashes.get(1));
          return cacheType(new StructType(typeDataHash, typeType(), name, fields, hashedDb, this));
      }
    } catch (HashedDbException e) {
      throw new ObjectsDbException(typeHash, e);
    }
  }

  private static void assertSize(Hash typeHash, String typeName, List<Hash> hashes,
      int expectedSize) {
    if (hashes.size() != expectedSize) {
      throw new ObjectsDbException(typeHash,
          "It is '" + typeName + "' type but its Merkle root has " + hashes.size() +
          " children when " + expectedSize + " is expected.");
    }
  }

  private Iterable<Field> readFields(Hash hash) throws HashedDbException {
    List<Field> result = new ArrayList<>();
    for (Hash fieldHash : hashedDb.readHashes(hash)) {
      List<Hash> hashes = hashedDb.readHashes(fieldHash, 2);
      String name = hashedDb.readString(hashes.get(0));
      ConcreteType type = getType(hashes.get(1));
      result.add(new Field(type, name, unknownLocation()));
    }
    return result;
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
}
