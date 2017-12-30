package org.smoothbuild.lang.type;

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

  @Inject
  public TypesDb(@Values HashedDb hashedDb) {
    this.hashedDb = hashedDb;
  }

  public TypesDb() {
    this(new HashedDb());
  }

  public TypeType type() {
    return new TypeType(writeBasicType("Type"), this);
  }

  public StringType string() {
    return new StringType(writeBasicType("String"), type());
  }

  public BlobType blob() {
    return new BlobType(writeBasicType("Blob"), type());
  }

  public NothingType nothing() {
    return new NothingType(writeBasicType("Nothing"), type());
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
      return new ArrayType(marshaller.hash(), type(), superType, elementType);
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
      return new StructType(marshaller.hash(), type(), name, fields);
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
    try (Unmarshaller unmarshaller = hashedDb.newUnmarshaller(hash)) {
      String name = hashedDb.readString(unmarshaller.readHash());
      switch (name) {
        case "Type":
          return new TypeType(hash, this);
        case "String":
          return new StringType(hash, type());
        case "Blob":
          return new BlobType(hash, type());
        case "Nothing":
          return new NothingType(hash, type());
        case "":
          Type elementType = read(unmarshaller.readHash());
          ArrayType superType = possiblyNullArrayType(elementType.superType());
          return new ArrayType(hash, type(), superType, elementType);
        default:
          return new StructType(hash, type(), name, readFields(unmarshaller.readHash()));
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
}
