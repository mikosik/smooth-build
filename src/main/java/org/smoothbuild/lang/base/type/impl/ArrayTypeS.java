package org.smoothbuild.lang.base.type.impl;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public final class ArrayTypeS extends TypeS implements ArrayType {
  private final TypeS elem;

  public ArrayTypeS(TypeS elem) {
    super(TypeNames.arrayTypeName(elem), elem.vars());
    this.elem = requireNonNull(elem);
  }

  @Override
  public TypeS elem() {
    return elem;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof ArrayTypeS thatArray
        && this.elem().equals(thatArray.elem());
  }
}
