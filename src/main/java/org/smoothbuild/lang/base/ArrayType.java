package org.smoothbuild.lang.base;

import com.google.inject.TypeLiteral;

public class ArrayType<T extends SValue> extends SType<Array<T>> {
  private final SType<T> elemSType;

  protected static <T extends SValue> ArrayType<T> arrayType(SType<T> elemType,
      TypeLiteral<Array<T>> jType) {
    return new ArrayType<>(elemType, jType);
  }

  protected ArrayType(SType<T> elemSType, TypeLiteral<Array<T>> jType) {
    super(elemSType.name() + "[]", jType);
    this.elemSType = elemSType;
  }

  public SType<T> elemType() {
    return elemSType;
  }
}
