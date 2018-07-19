package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.base.Parameter;
import org.smoothbuild.lang.base.Signature;
import org.smoothbuild.lang.value.Struct;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class StructType extends Type {
  private final ImmutableMap<String, Field> fields;
  private final Instantiator instantiator;

  public StructType(HashCode dataHash, TypeType type, String name, Iterable<Field> fields,
      Instantiator instantiator, HashedDb hashedDb) {
    this(dataHash, type, name, fieldsMap(fields), instantiator, hashedDb);
  }

  private static ImmutableMap<String, Field> fieldsMap(Iterable<Field> fields) {
    return stream(fields).collect(toImmutableMap(f -> f.name(), f -> f));
  }

  private StructType(HashCode dataHash, TypeType type, String name,
      ImmutableMap<String, Field> fields, Instantiator instantiator, HashedDb hashedDb) {
    super(dataHash, type, calculateSuperType(fields), name, Struct.class, hashedDb);
    this.fields = checkNotNull(fields);
    this.instantiator = checkNotNull(instantiator);
  }

  private static Type calculateSuperType(ImmutableMap<String, Field> fields) {
    if (fields.size() == 0) {
      return null;
    } else {
      Type superType = fields.values().iterator().next().type();
      if (superType.isArray() || superType.isNothing() || superType.isGeneric()) {
        throw new IllegalArgumentException();
      }
      return superType;
    }
  }

  @Override
  public Struct newValue(HashCode dataHash) {
    return new Struct(dataHash, this, instantiator, hashedDb);
  }

  public ImmutableMap<String, Field> fields() {
    return fields;
  }

  public Accessor accessor(String fieldName) {
    Field field = fields.get(fieldName);
    if (field == null) {
      throw new IllegalArgumentException("Struct " + name() + " doesn't have field " + fieldName);
    }
    return new Accessor(
        new Signature(field.type(), field.name(), list(new Parameter(this, "value", null))),
        field.name(),
        field.location());
  }
}
