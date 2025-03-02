package org.smoothbuild.compilerfrontend.lang.type;

import static java.util.Objects.requireNonNull;

/**
 * This class is immutable.
 */
public final class SArrayType extends SType {
  private final SType elem;

  public SArrayType(SType elem) {
    super(elem.typeVars());
    this.elem = requireNonNull(elem);
  }

  public SType elem() {
    return elem;
  }

  @Override
  public String specifier() {
    return "[" + elem.specifier() + "]";
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SArrayType thatArray && this.elem().equals(thatArray.elem());
  }
}
