package org.smoothbuild.lang.type;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.Struct;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class StructType extends Type {
  private final ImmutableMap<String, Type> fields;

  public StructType(HashCode hash, TypeType type, String name, ImmutableMap<String, Type> fields) {
    super(hash, type, name, Struct.class);
    this.fields = fields;
  }

  @Override
  public Struct newValue(HashCode hash, HashedDb hashedDb) {
    return new Struct(hash, this, hashedDb);
  }

  public ImmutableMap<String, Type> fields() {
    return fields;
  }

  @Override
  public Type directConvertibleTo() {
    return fields.values().iterator().next();
  }
}
