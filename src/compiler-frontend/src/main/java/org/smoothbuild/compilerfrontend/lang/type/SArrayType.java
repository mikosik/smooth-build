package org.smoothbuild.compilerfrontend.lang.type;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.collect.Set;

/**
 * This class is immutable.
 */
public final class SArrayType extends SType {
  private final SType elem;

  public SArrayType(SType elem) {
    this.elem = requireNonNull(elem);
  }

  public SType elem() {
    return elem;
  }

  @Override
  protected Set<STypeVar> calculateTypeVars() {
    return elem.typeVars();
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
