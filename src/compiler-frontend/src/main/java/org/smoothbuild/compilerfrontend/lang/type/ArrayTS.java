package org.smoothbuild.compilerfrontend.lang.type;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.compilerfrontend.lang.base.TypeNamesS;

/**
 * This class is immutable.
 */
public final class ArrayTS extends TypeS {
  private final TypeS elem;

  public ArrayTS(TypeS elem) {
    super(TypeNamesS.arrayTypeName(elem), elem.vars());
    this.elem = requireNonNull(elem);
  }

  public TypeS elem() {
    return elem;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof ArrayTS thatArray && this.elem().equals(thatArray.elem());
  }
}
