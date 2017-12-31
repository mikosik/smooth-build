package org.smoothbuild.lang.type;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
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
      type = new TypeType(writeBasicType("Type"), this);
      cache.put(type.hash(), type);
    }
    return type;
  }

  public StringType string() {
    if (string == null) {
      string = new StringType(writeBasicType("String"), type());
      cache.put(string.hash(), string);
    }
    return string;
  }

  public BlobType blob() {
    if (blob == null) {
      blob = new BlobType(writeBasicType("Blob"), type());
      cache.put(blob.hash(), blob);
    }
    return blob;
  }

  public NothingType nothing() {
    if (nothing == null) {
      nothing = new NothingType(writeBasicType("Nothing"), type());
      cache.put(nothing.hash(), nothing);
    }
    return nothing;
  }

  private HashCode writeBasicType(String name) {
    try (Marshaller marshaller = hashedDb.newMarshaller()) {
      marshaller.writeHash(hashedDb.writeString(name));
      marshaller.close();
      return marshaller.hash();
    }
  }

  public ArrayType array(Type elementType) {
    try (Marshaller marshaller = hashedDb.newMarshaller()) {
      marshaller.writeHash(hashedDb.writeString(""));
      marshaller.writeHash(elementType.hash());
      marshaller.close();
      ArrayType superType = possiblyNullArrayType(elementType.superType());
      return cache(new ArrayType(marshaller.hash(), type(), superType, elementType));
    }
  }

  private ArrayType possiblyNullArrayType(Type elementType) {
    return elementType == null ? null : array(elementType);
  }

  public StructType struct(String name, ImmutableMap<String, Type> fields) {
    try (Marshaller marshaller = hashedDb.newMarshaller()) {
      marshaller.writeHash(hashedDb.writeString(name));
      marshaller.writeHash(writeFields(fields));
      marshaller.close();
      return cache(new StructType(marshaller.hash(), type(), name, fields));
    }
  }

  private HashCode writeFields(ImmutableMap<String, Type> fields) {
    try (Marshaller marshaller = hashedDb.newMarshaller()) {
      for (Entry<String, Type> field : fields.entrySet()) {
        marshaller.writeHash(writeField(field.getKey(), field.getValue()));
      }
      marshaller.close();
      return marshaller.hash();
    }
  }

  private HashCode writeField(String name, Type type) {
    try (Marshaller marshaller = hashedDb.newMarshaller()) {
      marshaller.writeHash(hashedDb.writeString(name));
      marshaller.writeHash(type.hash());
      marshaller.close();
      return marshaller.hash();
    }
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
            return cache(new ArrayType(hash, type(), superType, elementType));
          default:
            return cache(new StructType(hash, type(), name, readFields(unmarshaller.readHash())));
        }
      }
    }
  }

  private ImmutableMap<String, Type> readFields(HashCode hash) {
    try (Unmarshaller unmarshaller = hashedDb.newUnmarshaller(hash)) {
      ImmutableMap.Builder<String, Type> builder = ImmutableMap.builder();
      HashCode elementHash = null;
      while ((elementHash = unmarshaller.tryReadHash()) != null) {
        try (Unmarshaller fieldUnmarshaller = hashedDb.newUnmarshaller(elementHash)) {
          HashCode nameHash = fieldUnmarshaller.readHash();
          HashCode typeHash = fieldUnmarshaller.readHash();
          builder.put(hashedDb.readString(nameHash), read(typeHash));
        }
      }
      return builder.build();
    }
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
