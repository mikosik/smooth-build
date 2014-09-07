package org.smoothbuild.lang.base;

import com.google.inject.TypeLiteral;

public class SArrayType<T extends SValue> extends SType<SArray<T>> {
  private final SType<T> elemSType;

  protected static <T extends SValue> SArrayType<T> sArrayType(SType<T> elemType,
      TypeLiteral<SArray<T>> jType) {
    return new SArrayType<>(elemType, jType);
  }

  protected SArrayType(SType<T> elemSType, TypeLiteral<SArray<T>> jType) {
    super(elemSType.name() + "[]", jType);
    this.elemSType = elemSType;
  }

  public SType<T> elemType() {
    return elemSType;
  }
}
