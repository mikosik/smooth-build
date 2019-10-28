package org.smoothbuild.lang.object.db;

import static org.smoothbuild.lang.object.base.Messages.ERROR;
import static org.smoothbuild.lang.object.base.Messages.INFO;
import static org.smoothbuild.lang.object.base.Messages.SEVERITY;
import static org.smoothbuild.lang.object.base.Messages.TEXT;
import static org.smoothbuild.lang.object.base.Messages.WARNING;

import java.io.IOException;

import javax.inject.Inject;

import org.smoothbuild.lang.object.base.ArrayBuilder;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.BlobBuilder;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.base.StructBuilder;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.object.type.StructType;
import org.smoothbuild.lang.plugin.Types;
import org.smoothbuild.util.DataWriter;

public class ObjectFactory {
  private final Types types;
  private final ObjectsDb objectsDb;

  @Inject
  public ObjectFactory(Types types, ObjectsDb objectsDb) {
    this.types = types;
    this.objectsDb = objectsDb;
  }

  public ArrayBuilder arrayBuilder(ConcreteType elementType) {
    return objectsDb.arrayBuilder(elementType);
  }

  public StructBuilder structBuilder(StructType type) {
    return objectsDb.structBuilder(type);
  }

  public Struct file(SString path, Blob content) {
    return structBuilder(types.file())
        .set("content", content)
        .set("path", path)
        .build();
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
    return objectsDb.structBuilder(types.message())
        .set(TEXT, textObject)
        .set(SEVERITY, severityObject)
        .build();
  }

  public BlobBuilder blobBuilder() {
    return objectsDb.blobBuilder();
  }

  public Blob blob(DataWriter dataInjector) throws IOException {
    try (BlobBuilder builder = blobBuilder()) {
      dataInjector.writeTo(builder.sink());
      return builder.build();
    }
  }

  public Bool bool(boolean value) {
    return objectsDb.bool(value);
  }

  public SString string(String string) {
    return objectsDb.string(string);
  }
}
