package org.smoothbuild.lang.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.hashed.Unmarshaller;
import org.smoothbuild.db.values.Values;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class TypesDb {
  private final HashedDb hashedDb;
  private final Map<HashCode, Type> cache;
  private TypeType type;
  private StringType string;
  private BlobType blob;
  private NothingType nothing;

  @Inject
  public TypesDb(@Values HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.cache = new HashMap<>();
  }

  public TypesDb() {
    this(new HashedDb());
  }

  public TypeType type() {
    if (type == null) {
      type = new TypeType(writeBasicType("Type"), this, hashedDb);
      cache.put(type.hash(), type);
    }
    return type;
  }

  public StringType string() {
    if (string == null) {
      string = new StringType(writeBasicType("String"), type(), hashedDb);
      cache.put(string.hash(), string);
    }
    return string;
  }

  public BlobType blob() {
    if (blob == null) {
      blob = new BlobType(writeBasicType("Blob"), type(), hashedDb);
      cache.put(blob.hash(), blob);
    }
    return blob;
  }

  public NothingType nothing() {
    if (nothing == null) {
      nothing = new NothingType(writeBasicType("Nothing"), type(), hashedDb);
      cache.put(nothing.hash(), nothing);
    }
    return nothing;
  }

  private HashCode writeBasicType(String name) {
    return hashedDb.writeHashes(hashedDb.writeString(name));
  }

  public ArrayType array(Type elementType) {
    HashCode hash = hashedDb.writeHashes(hashedDb.writeString(""), elementType.hash());
    ArrayType superType = possiblyNullArrayType(elementType.superType());
    return cache(new ArrayType(hash, type(), superType, elementType, hashedDb));
  }

  private ArrayType possiblyNullArrayType(Type elementType) {
    return elementType == null ? null : array(elementType);
  }

  public StructType struct(String name, ImmutableMap<String, Type> fields) {
    HashCode hash = hashedDb.writeHashes(hashedDb.writeString(name), writeFields(fields));
    return cache(new StructType(hash, type(), name, fields, hashedDb));
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
      try (Unmarshaller unmarshaller = hashedDb.newUnmarshaller(hash)) {
        String name = hashedDb.readString(unmarshaller.readHash());
        switch (name) {
          case "Type":
            return type();
          case "String":
            return string();
          case "Blob":
            return blob();
          case "Nothing":
            return nothing();
          case "":
            Type elementType = read(unmarshaller.readHash());
            ArrayType superType = possiblyNullArrayType(elementType.superType());
            return cache(new ArrayType(hash, type(), superType, elementType, hashedDb));
          default:
            ImmutableMap<String, Type> fields = readFields(unmarshaller.readHash());
            return cache(new StructType(hash, type(), name, fields, hashedDb));
        }
      }
    }
  }

  private ImmutableMap<String, Type> readFields(HashCode hash) {
    ImmutableMap.Builder<String, Type> builder = ImmutableMap.builder();
    for (HashCode fieldHash : hashedDb.readHashes(hash)) {
      List<HashCode> hashes = hashedDb.readHashes(fieldHash);
      if (hashes.size() != 2) {
        throw new HashedDbException("Corrupted field data");
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
}
