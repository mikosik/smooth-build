package org.smoothbuild.lang.object.db;

import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.type.Types.isGenericTypeName;
import static org.smoothbuild.lang.object.base.Messages.ERROR;
import static org.smoothbuild.lang.object.base.Messages.INFO;
import static org.smoothbuild.lang.object.base.Messages.SEVERITY;
import static org.smoothbuild.lang.object.base.Messages.TEXT;
import static org.smoothbuild.lang.object.base.Messages.WARNING;
import static org.smoothbuild.lang.object.type.TypeNames.FILE;
import static org.smoothbuild.lang.object.type.TypeNames.MESSAGE;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.object.base.ArrayBuilder;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.BlobBuilder;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.base.StructBuilder;
import org.smoothbuild.lang.object.type.ArrayType;
import org.smoothbuild.lang.object.type.BlobType;
import org.smoothbuild.lang.object.type.BoolType;
import org.smoothbuild.lang.object.type.ConcreteArrayType;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.object.type.Field;
import org.smoothbuild.lang.object.type.GenericArrayType;
import org.smoothbuild.lang.object.type.GenericType;
import org.smoothbuild.lang.object.type.NothingType;
import org.smoothbuild.lang.object.type.StringType;
import org.smoothbuild.lang.object.type.StructType;
import org.smoothbuild.lang.object.type.Type;
import org.smoothbuild.util.io.DataWriter;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 * Builders returned by xxxBuilder() methods are not thread-safe.
 */
@Singleton
public class ObjectFactory {
  private final ObjectDb objectDb;
  private final ConcurrentHashMap<String, ConcreteType> cache;
  private final StructType messageType;
  private final StructType fileType;

  @Inject
  public ObjectFactory(ObjectDb objectDb) {
    this.objectDb = objectDb;
    this.messageType = createMessageType(objectDb);
    this.fileType = createFileType(objectDb);
    this.cache = createInitializedCache(objectDb);
  }

  private static StructType createMessageType(ObjectDb objectDb) {
    StringType stringType = objectDb.stringType();
    Field text = new Field(stringType, "text", internal());
    Field severity = new Field(stringType, "severity", internal());
    return objectDb.structType(MESSAGE, ImmutableList.of(text, severity));
  }

  private static StructType createFileType(ObjectDb objectDb) {
    Field content = new Field(objectDb.blobType(), "content", internal());
    Field path = new Field(objectDb.stringType(), "path", internal());
    return objectDb.structType(FILE, ImmutableList.of(content, path));
  }

  private static ConcurrentHashMap<String, ConcreteType> createInitializedCache(ObjectDb objectDb) {
    ConcurrentHashMap<String, ConcreteType> map = new ConcurrentHashMap<>();
    putType(map, objectDb.blobType());
    putType(map, objectDb.boolType());
    putType(map, objectDb.nothingType());
    putType(map, objectDb.stringType());
    return map;
  }

  private static void putType(Map<String, ConcreteType> map, ConcreteType type) {
    map.put(type.name(), type);
  }

  public ArrayBuilder arrayBuilder(ConcreteType elementType) {
    return objectDb.arrayBuilder(elementType);
  }

  public Blob blob(DataWriter dataWriter) throws IOException {
    try (BlobBuilder builder = blobBuilder()) {
      dataWriter.writeTo(builder.sink());
      return builder.build();
    }
  }

  public BlobBuilder blobBuilder() {
    return objectDb.blobBuilder();
  }

  public Bool bool(boolean value) {
    return objectDb.bool(value);
  }

  public Struct file(SString path, Blob content) {
    return structBuilder(fileType())
        .set("content", content)
        .set("path", path)
        .build();
  }

  public SString string(String string) {
    return objectDb.string(string);
  }

  public StructBuilder structBuilder(StructType type) {
    return objectDb.structBuilder(type);
  }

  public ArrayType arrayType(Type elementType) {
    if (elementType.isGeneric()) {
      return arrayType((GenericType) elementType);
    } else {
      return arrayType((ConcreteType) elementType);
    }
  }

  public ConcreteArrayType arrayType(ConcreteType elementType) {
    return objectDb.arrayType(elementType);
  }

  public GenericArrayType arrayType(GenericType elementType) {
    return new GenericArrayType(elementType);
  }

  public BlobType blobType() {
    return objectDb.blobType();
  }

  public BoolType boolType() {
    return objectDb.boolType();
  }

  public StructType fileType() {
    return fileType;
  }

  public StructType messageType() {
    return messageType;
  }

  public NothingType nothingType() {
    return objectDb.nothingType();
  }

  public StringType stringType() {
    return objectDb.stringType();
  }

  public StructType structType(String name, Iterable<Field> fields) {
    StructType type = objectDb.structType(name, fields);
    cache.putIfAbsent(name, type);
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
    SObject textObject = objectDb.string(text);
    SObject severityObject = objectDb.string(severity);
    return objectDb.structBuilder(messageType())
        .set(TEXT, textObject)
        .set(SEVERITY, severityObject)
        .build();
  }

  public boolean containsType(String name) {
    return cache.containsKey(name);
  }
}
