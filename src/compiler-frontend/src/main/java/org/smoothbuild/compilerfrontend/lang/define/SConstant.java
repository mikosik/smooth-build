package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Smooth constant.
 */
public sealed interface SConstant extends SExpr permits SBlob, SInt, SString {
  @Override
  public default SType evaluationType() {
    return type();
  }

  public SType type();
}
