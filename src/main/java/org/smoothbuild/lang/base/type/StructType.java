package org.smoothbuild.lang.base.type;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.Streams.stream;

import java.util.Objects;

import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.base.Location;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * This class is immutable.
 */
public class StructType extends Type {
  private final ImmutableMap<String, Field> fields;

  public StructType(String name, Location location, ImmutableList<Field> fields) {
    this(name, location, fieldsMap(fields));
  }

  public StructType(String name, Location location, ImmutableMap<String, Field> fields) {
    super(name, location, calculateSuperType(fields), false);
    this.fields = fields;
  }

  private static ImmutableMap<String, Field> fieldsMap(Iterable<Field> fields) {
    return stream(fields).collect(toImmutableMap(Field::name, f -> f));
  }

  private static Type calculateSuperType(ImmutableMap<String, Field> fields) {
    if (fields.size() == 0) {
      return null;
    } else {
      Type superType = fields.values().iterator().next().type();
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
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object instanceof StructType thatStruct) {
      return this.name().equals(thatStruct.name())
          && this.fields().equals(thatStruct.fields());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name());
  }
}
