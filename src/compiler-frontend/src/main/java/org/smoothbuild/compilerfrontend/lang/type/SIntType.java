package org.smoothbuild.compilerfrontend.lang.type;

import org.smoothbuild.compilerfrontend.lang.name.Fqn;

/**
 * This class is immutable.
 */
public final class SIntType extends SBaseType {
  public SIntType() {
    super(Fqn.fqn("Int"));
  }
}
