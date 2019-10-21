package org.smoothbuild.lang.value;

import static org.smoothbuild.lang.message.Messages.ERROR;
import static org.smoothbuild.lang.message.Messages.INFO;
import static org.smoothbuild.lang.message.Messages.SEVERITY;
import static org.smoothbuild.lang.message.Messages.TEXT;
import static org.smoothbuild.lang.message.Messages.WARNING;

import java.io.IOException;

import javax.inject.Inject;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.plugin.Types;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.util.DataWriter;

public class ValueFactory {
  private final Types types;
  private final ValuesDb valuesDb;

  @Inject
  public ValueFactory(Types types, ValuesDb valuesDb) {
    this.types = types;
    this.valuesDb = valuesDb;
  }

  public ArrayBuilder arrayBuilder(ConcreteType elementType) {
    return valuesDb.arrayBuilder(elementType);
  }

  public StructBuilder structBuilder(StructType type) {
    return valuesDb.structBuilder(type);
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
    Value textValue = valuesDb.string(text);
    Value severityValue = valuesDb.string(severity);
    return valuesDb.structBuilder(types.message())
        .set(TEXT, textValue)
        .set(SEVERITY, severityValue)
        .build();
  }

  public BlobBuilder blobBuilder() {
    return valuesDb.blobBuilder();
  }

  public Blob blob(DataWriter dataInjector) throws IOException {
    try (BlobBuilder builder = blobBuilder()) {
      dataInjector.writeTo(builder.sink());
      return builder.build();
    }
  }

  public Bool bool(boolean value) {
    return valuesDb.bool(value);
  }

  public SString string(String string) {
    return valuesDb.string(string);
  }
}
