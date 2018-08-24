package org.smoothbuild.lang.value;

import java.io.IOException;

import javax.inject.Inject;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.plugin.Types;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.util.DataInjector;

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

  public BlobBuilder blobBuilder() {
    return valuesDb.blobBuilder();
  }

  public Blob blob(DataInjector dataInjector) throws IOException {
    try (BlobBuilder builder = blobBuilder()) {
      dataInjector.injectTo(builder.sink());
      return builder.build();
    }
  }

  public SString string(String string) {
    return valuesDb.string(string);
  }
}
