package org.smoothbuild.lang.type;

import java.util.Objects;

import org.smoothbuild.lang.value.Array;

public class ArrayType extends Type {
  private final Type elemType;

  protected ArrayType(Type elemType) {
    super("[" + elemType.name() + "]", Array.class);
    this.elemType = elemType;
  }

  public Type elemType() {
    return elemType;
  }

  @Override
  public final boolean equals(Object object) {
    if (object == null) {
      return false;
    }
    if (!ArrayType.class.equals(object.getClass())) {
      return false;
    }
    ArrayType that = (ArrayType) object;
    return this.name().equals(that.name()) && this.elemType.equals(that.elemType);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(13, elemType.hashCode());
  }
}
