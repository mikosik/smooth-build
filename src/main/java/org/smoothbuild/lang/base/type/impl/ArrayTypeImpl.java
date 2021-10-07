package org.smoothbuild.lang.base.type.impl;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class ArrayTypeImpl extends AbstractType implements ArrayType {
  private final Type elemType;

  public ArrayTypeImpl(Type elemType) {
    super(TypeNames.arrayTypeName(elemType), elemType.variables());
    this.elemType = requireNonNull(elemType);
  }

  @Override
  public Type elemType() {
    return elemType;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof ArrayTypeImpl thatArray
        && this.elemType().equals(thatArray.elemType());
  }
}
