package org.smoothbuild.lang.type;

import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.lang.message.Location.unknownLocation;
import static org.smoothbuild.lang.type.TypeNames.BLOB;
import static org.smoothbuild.lang.type.TypeNames.STRING;
import static org.smoothbuild.lang.type.TypeNames.TYPE;
import static org.smoothbuild.lang.type.TypeNames.isGenericTypeName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Unmarshaller;
import org.smoothbuild.db.values.CorruptedValueException;
import org.smoothbuild.db.values.Values;
import org.smoothbuild.lang.function.Field;

import com.google.common.hash.HashCode;

public class TypesDb {
  private final HashedDb hashedDb;
  private final Map<HashCode, Type> cache;
  private final Instantiator instantiator;
  private TypeType type;
  private StringType string;
  private BlobType blob;

  public TypesDb(@Values HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.cache = new HashMap<>();
    this.instantiator = new Instantiator(hashedDb, this);
  }

  public TypeType type() {
    if (type == null) {
      type = new TypeType(writeBasicTypeData(TYPE), this, hashedDb);
      cache.put(type.hash(), type);
    }
    return type;
  }

  public StringType string() {
    if (string == null) {
      string = new StringType(writeBasicTypeData(STRING), type(), hashedDb);
      cache.put(string.hash(), string);
    }
    return string;
  }

  public BlobType blob() {
    if (blob == null) {
      blob = new BlobType(writeBasicTypeData(BLOB), type(), hashedDb);
      cache.put(blob.hash(), blob);
    }
    return blob;
  }

  public GenericType generic(String name) {
    GenericType generic = new GenericType(writeBasicTypeData(name), type(), name, hashedDb);
    cache.put(generic.hash(), generic);
    return generic;
  }

  private HashCode writeBasicTypeData(String name) {
    return hashedDb.writeHashes(hashedDb.writeString(name));
  }

  public ArrayType array(Type elementType) {
    HashCode dataHash = hashedDb.writeHashes(hashedDb.writeString(""), elementType.hash());
    ArrayType superType = possiblyNullArrayType(elementType.superType());
    return cache(new ArrayType(dataHash, type(), superType, elementType, instantiator, hashedDb));
  }

  private ArrayType possiblyNullArrayType(Type elementType) {
    return elementType == null ? null : array(elementType);
  }

  public StructType struct(String name, Iterable<Field> fields) {
    HashCode hash = hashedDb.writeHashes(hashedDb.writeString(name), writeFields(fields));
    return cache(new StructType(hash, type(), name, fields, instantiator, hashedDb));
  }

  private HashCode writeFields(Iterable<Field> fields) {
    return hashedDb.writeHashes(
        stream(fields)
            .map(f -> writeField(f.name(), f.type()))
            .toArray(HashCode[]::new));
  }

  private HashCode writeField(String name, Type type) {
    return hashedDb.writeHashes(hashedDb.writeString(name), type.hash());
  }

  public Type read(HashCode hash) {
    if (cache.containsKey(hash)) {
      return cache.get(hash);
    } else {
      List<HashCode> hashes = hashedDb.readHashes(hash);
      switch (hashes.size()) {
        case 1:
          if (!type().hash().equals(hash)) {
            throw new CorruptedValueException(
                "Expected " + type() + " value but got value which hash is " + hash);
          }
          return type();
        case 2:
          HashCode typeHash = hashes.get(0);
          if (!type().hash().equals(typeHash)) {
            throw new CorruptedValueException(
                "Expected " + type() + " value but got value which hash is " + typeHash);
          }
          HashCode dataHash = hashes.get(1);
          return readFromDataHash(dataHash);
        default:
          throw newCorruptedMerkleRootException(hash, hashes.size());
      }
    }
  }

  protected Type readFromDataHash(HashCode typeDataHash) {
    try (Unmarshaller unmarshaller = hashedDb.newUnmarshaller(typeDataHash)) {
      String name = hashedDb.readString(unmarshaller.readHash());
      switch (name) {
        case STRING:
          return string();
        case BLOB:
          return blob();
        case "":
          Type elementType = read(unmarshaller.readHash());
          ArrayType superType = possiblyNullArrayType(elementType.superType());
          return cache(new ArrayType(typeDataHash, type(), superType, elementType, instantiator,
              hashedDb));
        default:
      }
      if (isGenericTypeName(name)) {
        return generic(name);
      } else {
        Iterable<Field> fields = readFields(unmarshaller.readHash());
        return cache(new StructType(typeDataHash, type(), name, fields, instantiator, hashedDb));
      }
    }
  }

  private Iterable<Field> readFields(HashCode hash) {
    List<Field> result = new ArrayList<>();
    for (HashCode fieldHash : hashedDb.readHashes(hash)) {
      List<HashCode> hashes = hashedDb.readHashes(fieldHash);
      if (hashes.size() != 2) {
        throw newCorruptedMerkleRootException(hash, hashes.size());
      }
      String name = hashedDb.readString(hashes.get(0));
      Type type = read(hashes.get(1));
      result.add(new Field(type, name, unknownLocation()));
    }
    return result;
  }

  private <T extends Type> T cache(T type) {
    HashCode hash = type.hash();
    if (cache.containsKey(hash)) {
      return (T) cache.get(hash);
    } else {
      cache.put(hash, type);
      return type;
    }
  }

  private CorruptedValueException newCorruptedMerkleRootException(HashCode hash, int childCount) {
    return new CorruptedValueException(
        hash, "Its Merkle tree root has " + childCount + " children.");
  }
}
