package org.smoothbuild.lang.type;

import com.google.inject.TypeLiteral;

public class SArrayType<T extends SValue> extends SType<SArray<T>> {
  private final SType<T> elemType;

  protected SArrayType(String name, TypeLiteral<SArray<T>> javaType, SType<T> elemType,
      SType<?>... superTypes) {
    super(name, javaType, superTypes);
    this.elemType = elemType;
  }

  public SType<T> elemType() {
    return elemType;
  }
}
