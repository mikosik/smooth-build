package org.smoothbuild.lang.type;

import static org.smoothbuild.util.Lists.list;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Unmarshaller;
import org.smoothbuild.db.values.CorruptedValueException;
import org.smoothbuild.db.values.Values;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class TypesDb {
  private final HashedDb hashedDb;
  private final Map<HashCode, Type> cache;
  private final Instantiator instantiator;
  private TypeType type;
  private StringType string;
  private BlobType blob;
  private GenericType generic;

  public TypesDb(@Values HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.cache = new HashMap<>();
    this.instantiator = new Instantiator(hashedDb, this);
  }

  public Type nonArrayTypeFromString(String string) {
    for (Type type : list(string(), blob())) {
      if (type.name().equals(string)) {
        return type;
      }
    }
    return null;
  }

  public TypeType type() {
    if (type == null) {
      type = new TypeType(writeBasicTypeData("Type"), this, hashedDb);
      cache.put(type.hash(), type);
    }
    return type;
  }

  public StringType string() {
    if (string == null) {
      string = new StringType(writeBasicTypeData("String"), type(), hashedDb);
      cache.put(string.hash(), string);
    }
    return string;
  }

  public BlobType blob() {
    if (blob == null) {
      blob = new BlobType(writeBasicTypeData("Blob"), type(), hashedDb);
      cache.put(blob.hash(), blob);
    }
    return blob;
  }

  public GenericType generic() {
    if (generic == null) {
      generic = new GenericType(writeBasicTypeData("a"), type(), hashedDb);
      cache.put(generic.hash(), generic);
    }
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

  public StructType struct(String name, ImmutableMap<String, Type> fields) {
    HashCode hash = hashedDb.writeHashes(hashedDb.writeString(name), writeFields(fields));
    return cache(new StructType(hash, type(), name, fields, instantiator, hashedDb));
  }

  private HashCode writeFields(ImmutableMap<String, Type> fields) {
    return hashedDb.writeHashes(
        fields
            .entrySet()
            .stream()
            .map(f -> writeField(f.getKey(), f.getValue()))
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
        case "String":
          return string();
        case "Blob":
          return blob();
        case "a":
          return generic();
        case "":
          Type elementType = read(unmarshaller.readHash());
          ArrayType superType = possiblyNullArrayType(elementType.superType());
          return cache(new ArrayType(typeDataHash, type(), superType, elementType, instantiator,
              hashedDb));
        default:
          ImmutableMap<String, Type> fields = readFields(unmarshaller.readHash());
          return cache(new StructType(typeDataHash, type(), name, fields, instantiator, hashedDb));
      }
    }
  }

  private ImmutableMap<String, Type> readFields(HashCode hash) {
    ImmutableMap.Builder<String, Type> builder = ImmutableMap.builder();
    for (HashCode fieldHash : hashedDb.readHashes(hash)) {
      List<HashCode> hashes = hashedDb.readHashes(fieldHash);
      if (hashes.size() != 2) {
        throw newCorruptedMerkleRootException(hash, hashes.size());
      }
      String fieldName = hashedDb.readString(hashes.get(0));
      Type fieldType = read(hashes.get(1));
      builder.put(fieldName, fieldType);
    }
    return builder.build();
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
