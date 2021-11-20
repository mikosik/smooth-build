package org.smoothbuild.lang.base.type.impl;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public final class ArrayTypeS extends TypeS implements ArrayType {
  private final TypeS element;

  public ArrayTypeS(TypeS element) {
    super(TypeNames.arrayTypeName(element), element.variables());
    this.element = requireNonNull(element);
  }

  @Override
  public TypeS element() {
    return element;
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
