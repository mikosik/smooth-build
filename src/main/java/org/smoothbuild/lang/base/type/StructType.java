package org.smoothbuild.lang.base.type;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.object.db.ObjectFactory;
import org.smoothbuild.lang.object.type.Type;

import com.google.common.collect.ImmutableList;

public record StructType(String name, ImmutableList<Field>fields) implements ConcreteType {
  public StructType {
    requireNonNull(name);
    requireNonNull(fields);
  }

  @Override
  public Type toDType(ObjectFactory objectFactory) {
    Iterable<org.smoothbuild.lang.object.type.Field> rFields = fields.stream()
        .map(f -> new org.smoothbuild.lang.object.type.Field(
            (org.smoothbuild.lang.object.type.ConcreteType) f.type().toDType(objectFactory),
            f.name(),
            null))
        .collect(toImmutableList());
    return objectFactory.structType(name, rFields);
  }

  @Override
  public String q() {
    return "'" + name + "'";
  }
}
