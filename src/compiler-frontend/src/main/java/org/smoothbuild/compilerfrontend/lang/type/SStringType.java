package org.smoothbuild.compilerfrontend.lang.type;

import org.smoothbuild.compilerfrontend.lang.name.Fqn;

/**
 * This class is immutable.
 */
public final class SStringType extends SBaseType {
  public SStringType() {
    super(Fqn.fqn("String"));
  }
}
