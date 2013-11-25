package org.smoothbuild.lang.type;

import com.google.inject.TypeLiteral;

public class ArrayType<T extends Value> extends Type<SArray<T>> {
  protected ArrayType(String name, TypeLiteral<SArray<T>> javaType, ArrayType<?>... superTypes) {
    super(name, javaType, superTypes);
  }
}
