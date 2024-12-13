package org.smoothbuild.compilerfrontend.lang.type;

import org.smoothbuild.compilerfrontend.lang.base.Id;

/**
 * This class is immutable.
 */
public final class SBoolType extends SBaseType {
  public SBoolType() {
    super(Id.id("Bool"));
  }
}
