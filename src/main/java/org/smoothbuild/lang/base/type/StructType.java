package org.smoothbuild.lang.base.type;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.Streams.stream;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.compound.StructCompoundability;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class StructType extends ConcreteType {
  private final ImmutableMap<String, Field> fields;

  public StructType(String name, Location location, ImmutableList<Field> fields) {
    this(name, location, fieldsMap(fields));
  }

  public StructType(String name, Location location, ImmutableMap<String, Field> fields) {
    super(name, location, calculateSuperType(fields), new StructCompoundability());
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
}
