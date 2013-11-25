package org.smoothbuild.lang.type;

import com.google.inject.TypeLiteral;

public class SArrayType<T extends SValue> extends SType<SArray<T>> {
  protected SArrayType(String name, TypeLiteral<SArray<T>> javaType, SArrayType<?>... superTypes) {
    super(name, javaType, superTypes);
  }
}
