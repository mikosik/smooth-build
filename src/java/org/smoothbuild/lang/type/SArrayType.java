package org.smoothbuild.lang.type;

import com.google.inject.TypeLiteral;

public class SArrayType<T extends SValue> extends SType<SArray<T>> {
  private final SType<T> elemType;

  protected static <T extends SValue> SArrayType<T> sArrayType(TypeLiteral<SArray<T>> javaType,
      SType<T> elemType) {
    return new SArrayType<T>(javaType, elemType);
  }

  protected SArrayType(TypeLiteral<SArray<T>> javaType, SType<T> elemType) {
    super(elemType.name() + "[]", javaType);
    this.elemType = elemType;
  }

  public SType<T> elemType() {
    return elemType;
  }
}
