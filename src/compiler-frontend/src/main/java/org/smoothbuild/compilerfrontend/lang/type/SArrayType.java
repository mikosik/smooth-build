package org.smoothbuild.compilerfrontend.lang.type;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.compilerfrontend.lang.name.TokenNames.arrayTypeName;

/**
 * This class is immutable.
 */
public final class SArrayType extends SType {
  private final SType elem;

  public SArrayType(SType elem) {
    super(elem.vars());
    this.elem = requireNonNull(elem);
  }

  public SType elem() {
    return elem;
  }

  @Override
  public String specifier() {
    return arrayTypeName(elem);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SArrayType thatArray && this.elem().equals(thatArray.elem());
  }
}
