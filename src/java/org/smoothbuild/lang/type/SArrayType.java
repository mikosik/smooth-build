package org.smoothbuild.lang.type;

import com.google.inject.TypeLiteral;

public class SArrayType<T extends SValue> extends SType<SArray<T>> {
  private final SType<T> elemType;

  protected SArrayType(TypeLiteral<SArray<T>> javaType, SType<T> elemType, SType<?>... superTypes) {
    super(elemType.name() + "[]", javaType, superTypes);
    this.elemType = elemType;
  }

  public SType<T> elemType() {
    return elemType;
  }
}
