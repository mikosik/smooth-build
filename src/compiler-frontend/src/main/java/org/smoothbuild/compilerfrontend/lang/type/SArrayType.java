package org.smoothbuild.compilerfrontend.lang.type;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.compilerfrontend.lang.base.TypeNamesS;

/**
 * This class is immutable.
 */
public final class SArrayType extends SType {
  private final SType elem;

  public SArrayType(SType elem) {
    super(TypeNamesS.arrayTypeName(elem), elem.vars());
    this.elem = requireNonNull(elem);
  }

  public SType elem() {
    return elem;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SArrayType thatArray && this.elem().equals(thatArray.elem());
  }
}
