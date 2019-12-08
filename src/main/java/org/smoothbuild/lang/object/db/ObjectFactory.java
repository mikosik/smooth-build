package org.smoothbuild.lang.object.db;

import static org.smoothbuild.lang.object.base.Messages.ERROR;
import static org.smoothbuild.lang.object.base.Messages.INFO;
import static org.smoothbuild.lang.object.base.Messages.SEVERITY;
import static org.smoothbuild.lang.object.base.Messages.TEXT;
import static org.smoothbuild.lang.object.base.Messages.WARNING;
import static org.smoothbuild.lang.object.type.TypeNames.isGenericTypeName;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.object.base.ArrayBuilder;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.BlobBuilder;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.base.StructBuilder;
import org.smoothbuild.lang.object.type.ArrayType;
import org.smoothbuild.lang.object.type.ConcreteArrayType;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.object.type.GenericArrayType;
import org.smoothbuild.lang.object.type.GenericType;
import org.smoothbuild.lang.object.type.StructType;
import org.smoothbuild.lang.object.type.Type;
import org.smoothbuild.lang.object.type.TypeNames;
import org.smoothbuild.util.DataWriter;

/**
 * This class is thread-safe.
 */
@Singleton
public class ObjectFactory {
  private final ObjectsDb objectsDb;
  private final ConcurrentHashMap<String, ConcreteType> cache;

  @Inject
  public ObjectFactory(ObjectsDb objectsDb) {
    this.objectsDb = objectsDb;
    this.cache = createInitializedCache(objectsDb);
  }

  private static ConcurrentHashMap<String, ConcreteType> createInitializedCache(ObjectsDb objectsDb) {
    ConcurrentHashMap<String, ConcreteType> map = new ConcurrentHashMap<>();
    putType(map, objectsDb.boolType());
    putType(map, objectsDb.stringType());
    putType(map, objectsDb.blobType());
    putType(map, objectsDb.nothingType());
    return map;
  }

  private static void putType(Map<String, ConcreteType> map, ConcreteType type) {
    map.put(type.name(), type);
  }

  public ArrayBuilder arrayBuilder(ConcreteType elementType) {
    return objectsDb.arrayBuilder(elementType);
  }

  public Blob blob(DataWriter dataWriter) throws IOException {
    try (BlobBuilder builder = blobBuilder()) {
      dataWriter.writeTo(builder.sink());
      return builder.build();
    }
  }

  public BlobBuilder blobBuilder() {
    return objectsDb.blobBuilder();
  }

  public Bool bool(boolean value) {
    return objectsDb.bool(value);
  }

  public Struct file(SString path, Blob content) {
    return structBuilder(fileType())
        .set("content", content)
        .set("path", path)
        .build();
  }

  public SString string(String string) {
    return objectsDb.string(string);
  }

  public StructBuilder structBuilder(StructType type) {
    return objectsDb.structBuilder(type);
  }

  public ArrayType arrayType(Type elementType) {
    if (elementType.isGeneric()) {
      return arrayType((GenericType) elementType);
    } else {
      return arrayType((ConcreteType) elementType);
    }
  }

  public ConcreteArrayType arrayType(ConcreteType elementType) {
    return objectsDb.arrayType(elementType);
  }

  public GenericArrayType arrayType(GenericType elementType) {
    return new GenericArrayType(elementType);
  }

  public ConcreteType blobType() {
    return objectsDb.blobType();
  }

  public ConcreteType boolType() {
    return objectsDb.boolType();
  }

  public StructType fileType() {
    return (StructType) getType(TypeNames.FILE);
  }

  public StructType messageType() {
    return (StructType) getType(TypeNames.MESSAGE);
  }

  public ConcreteType nothingType() {
    return objectsDb.nothingType();
  }

  public ConcreteType stringType() {
    return objectsDb.stringType();
  }

  public StructType structType(String name, Iterable<Field> fields) {
    StructType type = objectsDb.structType(name, fields);
    ConcreteType previousValue = cache.putIfAbsent(name, type);
    if (previousValue != null) {
      throw new IllegalStateException("Type '" + name + "' is already added to runtime types.");
    }
    return type;
  }

  public Type getType(String name) {
    if (isGenericTypeName(name)) {
      return new GenericType(name);
    } else {
      ConcreteType type = cache.get(name);
      if (type == null) {
        throw new IllegalStateException("Unknown runtime type '" + name + "'.");
      }
      return type;
    }
  }

  public Struct errorMessage(String text) {
    return message(ERROR, text);
  }

  public Struct warningMessage(String text) {
    return message(WARNING, text);
  }

  public Struct infoMessage(String text) {
    return message(INFO, text);
  }

  private Struct message(String severity, String text) {
    SObject textObject = objectsDb.string(text);
    SObject severityObject = objectsDb.string(severity);
    return objectsDb.structBuilder(messageType())
        .set(TEXT, textObject)
        .set(SEVERITY, severityObject)
        .build();
  }

  public boolean containsType(String name) {
    return cache.containsKey(name);
  }
}
