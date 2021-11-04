package org.smoothbuild.lang.base.type.impl;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class ArrayTypeS extends TypeS implements ArrayType {
  private final TypeS elemType;

  public ArrayTypeS(TypeS elemType) {
    super(TypeNames.arrayTypeName(elemType), elemType.variables());
    this.elemType = requireNonNull(elemType);
  }

  @Override
  public TypeS element() {
    return elemType;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof ArrayTypeS thatArray
        && this.element().equals(thatArray.element());
  }
}
