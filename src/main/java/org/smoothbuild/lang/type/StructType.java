package org.smoothbuild.lang.type;

import java.util.Objects;

import org.smoothbuild.lang.value.Value;

public class StructType extends Type {
  private final Type directConvertibleTo;

  protected StructType(String name, Class<? extends Value> jType, Type directConvertibleTo) {
    super(name, jType);
    this.directConvertibleTo = directConvertibleTo;
  }

  @Override
  public Type directConvertibleTo() {
    return directConvertibleTo;
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
