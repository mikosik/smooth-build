package org.smoothbuild.lang.value;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.plugin.Types;
import org.smoothbuild.lang.runtime.RuntimeTypes;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypesDb;

import com.google.common.collect.ImmutableMap;

public class ValueFactory {
  private final Types types;
  private final ValuesDb valuesDb;

  @Inject
  public ValueFactory(Types types, ValuesDb valuesDb) {
    this.types = types;
    this.valuesDb = valuesDb;
  }

  public ValueFactory() {
    this(new HashedDb());
  }

  public ValueFactory(HashedDb hashedDb) {
    this(hashedDb, new TypesDb(hashedDb));
  }

  public ValueFactory(HashedDb hashedDb, TypesDb typesDb) {
    this(new RuntimeTypes(typesDb), new ValuesDb(hashedDb, typesDb));
  }

  public ValueFactory(RuntimeTypes types, ValuesDb valuesDb) {
    this((Types) types, valuesDb);
    types.struct("File", ImmutableMap.of("content", types.blob(), "path", types.string()));
  }

  public ArrayBuilder arrayBuilder(Type elementType) {
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

  public SString string(String string) {
    return valuesDb.string(string);
  }
}
