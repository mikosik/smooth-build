package org.smoothbuild.lang.base;

import com.google.inject.TypeLiteral;

public class SArrayType<T extends SValue> extends SType<SArray<T>> {
  private final SType<T> elemSType;

  protected static <T extends SValue> SArrayType<T> sArrayType(TypeLiteral<SArray<T>> jType,
      SType<T> elemType) {
    return new SArrayType<>(jType, elemType);
  }

  protected SArrayType(TypeLiteral<SArray<T>> jType, SType<T> elemSType) {
    super(elemSType.name() + "[]", jType);
    this.elemSType = elemSType;
  }

  public SType<T> elemType() {
    return elemSType;
  }
}
