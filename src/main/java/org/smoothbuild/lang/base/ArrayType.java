package org.smoothbuild.lang.base;

import com.google.inject.TypeLiteral;

public class ArrayType<T extends Value> extends Type<Array<T>> {
  private final Type<T> elemType;

  protected static <T extends Value> ArrayType<T> arrayType(Type<T> elemType,
      TypeLiteral<Array<T>> jType) {
    return new ArrayType<>(elemType, jType);
  }

  protected ArrayType(Type<T> elemType, TypeLiteral<Array<T>> jType) {
    super(elemType.name() + "[]", jType);
    this.elemType = elemType;
  }

  public Type<T> elemType() {
    return elemType;
  }
}
