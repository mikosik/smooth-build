package org.smoothbuild.lang.base;

import com.google.inject.TypeLiteral;

public class ArrayType extends Type {
  private final Type elemType;

  protected static ArrayType arrayType(Type elemType, TypeLiteral<? extends Value> jType) {
    return new ArrayType(elemType, jType);
  }

  protected ArrayType(Type elemType, TypeLiteral<? extends Value> jType) {
    super(elemType.name() + "[]", jType);
    this.elemType = elemType;
  }

  public Type elemType() {
    return elemType;
  }
}
