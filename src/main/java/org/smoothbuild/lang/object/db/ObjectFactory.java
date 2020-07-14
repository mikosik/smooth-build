package org.smoothbuild.lang.object.db;

import static org.smoothbuild.lang.object.base.Messages.ERROR;
import static org.smoothbuild.lang.object.base.Messages.INFO;
import static org.smoothbuild.lang.object.base.Messages.WARNING;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.object.base.ArrayBuilder;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.BlobBuilder;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.type.BlobType;
import org.smoothbuild.lang.object.type.BoolType;
import org.smoothbuild.lang.object.type.ConcreteArrayType;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.object.type.NothingType;
import org.smoothbuild.lang.object.type.StringType;
import org.smoothbuild.lang.object.type.StructType;
import org.smoothbuild.util.io.DataWriter;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 * Builders returned by xxxBuilder() methods are not thread-safe.
 */
@Singleton
public class ObjectFactory {
  private final ObjectDb objectDb;
  private final StructType messageType;
  private final StructType fileType;

  @Inject
  public ObjectFactory(ObjectDb objectDb) {
    this.objectDb = objectDb;
    this.messageType = createMessageType(objectDb);
    this.fileType = createFileType(objectDb);
  }

  private static StructType createMessageType(ObjectDb objectDb) {
    StringType stringType = objectDb.stringType();
    return objectDb.structType(ImmutableList.of(stringType, stringType));
  }

  private static StructType createFileType(ObjectDb objectDb) {
    return objectDb.structType(ImmutableList.of(objectDb.blobType(), objectDb.stringType()));
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
    return objectDb.struct(fileType(), ImmutableList.of(content, path));
  }

  public SString string(String string) {
    return objectDb.string(string);
  }

  public Struct struct(StructType type, Iterable<? extends SObject> fields) {
    return objectDb.struct(type, fields);
  }

  public ConcreteArrayType arrayType(ConcreteType elementType) {
    return objectDb.arrayType(elementType);
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

  public StructType structType(Iterable<? extends ConcreteType> fieldTypes) {
    return objectDb.structType(fieldTypes);
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
    return objectDb.struct(messageType(), ImmutableList.of(textObject, severityObject));
  }
}
