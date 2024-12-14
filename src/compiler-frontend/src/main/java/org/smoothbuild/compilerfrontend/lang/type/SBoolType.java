package org.smoothbuild.compilerfrontend.lang.type;

import org.smoothbuild.compilerfrontend.lang.base.Fqn;

/**
 * This class is immutable.
 */
public final class SBoolType extends SBaseType {
  public SBoolType() {
    super(Fqn.fqn("Bool"));
  }
}
