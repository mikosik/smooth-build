package org.smoothbuild.compilerfrontend.lang.type;

import org.smoothbuild.compilerfrontend.lang.base.Id;

/**
 * This class is immutable.
 */
public final class SStringType extends SBaseType {
  public SStringType() {
    super(Id.id("String"));
  }
}
