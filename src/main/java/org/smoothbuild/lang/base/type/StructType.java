package org.smoothbuild.lang.base.type;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.Streams.stream;

import java.util.Objects;

import org.smoothbuild.lang.object.db.ObjectFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class StructType extends ConcreteType {
  private final ImmutableMap<String, Field> fields;

  public StructType(String name, ImmutableList<Field> fields) {
    this(name, fieldsMap(fields));
  }

  public StructType(String name, ImmutableMap<String, Field> fields) {
    super(name, calculateSuperType(fields));
    this.fields = fields;
  }

  private static ImmutableMap<String, Field> fieldsMap(Iterable<Field> fields) {
    return stream(fields).collect(toImmutableMap(Field::name, f -> f));
  }

  private static ConcreteType calculateSuperType(ImmutableMap<String, Field> fields) {
    if (fields.size() == 0) {
      return null;
    } else {
      ConcreteType superType = fields.values().iterator().next().type();
      if (superType.isArray() || superType.isNothing()) {
        throw new IllegalArgumentException();
      }
      return superType;
    }
  }

  public ImmutableMap<String, Field> fields() {
    return fields;
  }

  @Override
  public org.smoothbuild.lang.object.type.Type toRecordType(ObjectFactory objectFactory) {
    Iterable<org.smoothbuild.lang.object.type.Field> rFields = fields.values().stream()
        .map(f -> new org.smoothbuild.lang.object.type.Field(
            (org.smoothbuild.lang.object.type.ConcreteType) f.type().toRecordType(objectFactory),
            f.name(),
            null))
        .collect(toImmutableList());
    return objectFactory.structType(name(), rFields);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof StructType that) {
      return this.name().equals(that.name())
          && this.fields.equals(that.fields);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name());
  }
}
