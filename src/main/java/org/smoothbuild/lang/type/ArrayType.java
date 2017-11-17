package org.smoothbuild.lang.type;

import java.util.Objects;

import org.smoothbuild.lang.value.Array;

public class ArrayType extends Type {
  private final Type elemType;

  public static ArrayType arrayOf(Type elemType) {
    return new ArrayType(elemType);
  }

  private ArrayType(Type elemType) {
    super("[" + elemType.name() + "]", Array.class);
    this.elemType = elemType;
  }

  public Type elemType() {
    return elemType;
  }

  @Override
  public Type coreType() {
    return elemType.coreType();
  }

  @Override
  public ArrayType directConvertibleTo() {
    Type elemConvertibleTo = elemType.directConvertibleTo();
    return elemConvertibleTo == null ? null : arrayOf(elemConvertibleTo);
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

  public static int depthOf(Type type) {
    if (type instanceof ArrayType) {
      return 1 + depthOf(((ArrayType) type).elemType);
    }
    return 0;
  }
}
