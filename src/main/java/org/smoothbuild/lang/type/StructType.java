package org.smoothbuild.lang.type;

import java.util.Objects;

import org.smoothbuild.lang.value.Value;

import com.google.common.collect.ImmutableMap;

public class StructType extends Type {
  private final ImmutableMap<String, Type> fields;

  protected StructType(String name, Class<? extends Value> jType,
      ImmutableMap<String, Type> fields) {
    super(name, jType);
    this.fields = fields;
  }

  public ImmutableMap<String, Type> fields() {
    return fields;
  }

  @Override
  public Type directConvertibleTo() {
    return fields.values().iterator().next();
  }

  @Override
  public boolean equals(Object object) {
    if (object == null) {
      return false;
    }
    if (!StructType.class.equals(object.getClass())) {
      return false;
    }
    Type that = (Type) object;
    return this.name().equals(that.name()) && this.jType().equals(that.jType());
  }

  @Override
  public int hashCode() {
    return Objects.hash(name(), jType());
  }
}
