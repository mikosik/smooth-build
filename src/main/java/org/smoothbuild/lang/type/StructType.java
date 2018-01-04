package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.Struct;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class StructType extends Type {
  private final ImmutableMap<String, Type> fields;
  private final Instantiator instantiator;

  public StructType(HashCode dataHash, TypeType type, String name,
      ImmutableMap<String, Type> fields, Instantiator instantiator, HashedDb hashedDb) {
    super(dataHash, type, calculateSuperType(fields), name, Struct.class, hashedDb);
    this.fields = checkNotNull(fields);
    this.instantiator = checkNotNull(instantiator);
  }

  private static Type calculateSuperType(ImmutableMap<String, Type> fields) {
    return fields.size() == 0 ? null : fields.values().iterator().next();
  }

  @Override
  public Struct newValue(HashCode dataHash) {
    return new Struct(dataHash, this, instantiator, hashedDb);
  }

  public ImmutableMap<String, Type> fields() {
    return fields;
  }
}
