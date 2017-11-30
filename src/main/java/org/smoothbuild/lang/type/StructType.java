package org.smoothbuild.lang.type;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.Value;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class StructType extends Type {
  private final ImmutableMap<String, Type> fields;

  protected StructType(String name, ImmutableMap<String, Type> fields) {
    super(name, Struct.class);
    this.fields = fields;
  }

  @Override
  public Value newValue(HashCode hash, HashedDb hashedDb) {
    return new Struct(this, hash, hashedDb);
  }

  public ImmutableMap<String, Type> fields() {
    return fields;
  }

  @Override
  public Type directConvertibleTo() {
    return fields.values().iterator().next();
  }
}
