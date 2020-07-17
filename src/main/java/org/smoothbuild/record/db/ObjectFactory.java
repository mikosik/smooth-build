package org.smoothbuild.record.db;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.record.base.ArrayBuilder;
import org.smoothbuild.record.base.Blob;
import org.smoothbuild.record.base.BlobBuilder;
import org.smoothbuild.record.base.Bool;
import org.smoothbuild.record.base.Messages;
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
import org.smoothbuild.util.io.DataWriter;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 * Builders returned by xxxBuilder() methods are not thread-safe.
 */
@Singleton
public class ObjectFactory {
  private final ObjectDb objectDb;
  private final TupleType messageType;
  private final TupleType fileType;

  @Inject
  public ObjectFactory(ObjectDb objectDb) {
    this.objectDb = objectDb;
    this.messageType = createMessageType(objectDb);
    this.fileType = createFileType(objectDb);
  }

  private static TupleType createMessageType(ObjectDb objectDb) {
    StringType stringType = objectDb.stringType();
    return objectDb.structType(ImmutableList.of(stringType, stringType));
  }

  private static TupleType createFileType(ObjectDb objectDb) {
    return objectDb.structType(ImmutableList.of(objectDb.blobType(), objectDb.stringType()));
  }

  public ArrayBuilder arrayBuilder(BinaryType elementType) {
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

  public Tuple file(SString path, Blob content) {
    return objectDb.struct(fileType(), ImmutableList.of(content, path));
  }

  public SString string(String string) {
    return objectDb.string(string);
  }

  public Tuple struct(TupleType type, Iterable<? extends SObject> fields) {
    return objectDb.struct(type, fields);
  }

  public ArrayType arrayType(BinaryType elementType) {
    return objectDb.arrayType(elementType);
  }

  public BlobType blobType() {
    return objectDb.blobType();
  }

  public BoolType boolType() {
    return objectDb.boolType();
  }

  public TupleType fileType() {
    return fileType;
  }

  public TupleType messageType() {
    return messageType;
  }

  public NothingType nothingType() {
    return objectDb.nothingType();
  }

  public StringType stringType() {
    return objectDb.stringType();
  }

  public TupleType structType(Iterable<? extends BinaryType> fieldTypes) {
    return objectDb.structType(fieldTypes);
  }

  public Tuple errorMessage(String text) {
    return message(Messages.ERROR, text);
  }

  public Tuple warningMessage(String text) {
    return message(Messages.WARNING, text);
  }

  public Tuple infoMessage(String text) {
    return message(Messages.INFO, text);
  }

  private Tuple message(String severity, String text) {
    SObject textObject = objectDb.string(text);
    SObject severityObject = objectDb.string(severity);
    return objectDb.struct(messageType(), ImmutableList.of(textObject, severityObject));
  }
}
