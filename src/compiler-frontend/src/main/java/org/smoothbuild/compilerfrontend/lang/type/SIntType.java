package org.smoothbuild.compilerfrontend.lang.type;

import org.smoothbuild.compilerfrontend.lang.base.Id;

/**
 * This class is immutable.
 */
public final class SIntType extends SBaseType {
  public SIntType() {
    super(Id.id("Int"));
  }
}
