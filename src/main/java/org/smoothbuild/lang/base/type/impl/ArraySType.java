package org.smoothbuild.lang.base.type.impl;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class ArraySType extends SType implements ArrayType {
  private final Type elemType;

  public ArraySType(Type elemType) {
    super(TypeNames.arrayTypeName(elemType), elemType.variables());
    this.elemType = requireNonNull(elemType);
  }

  @Override
  public Type element() {
    return elemType;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof ArraySType thatArray
        && this.element().equals(thatArray.element());
  }
}