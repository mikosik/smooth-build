package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.type.TypeS;

/**
 * Smooth constant.
 */
public sealed interface ConstantS extends ExprS permits BlobS, IntS, StringS {
  @Override
  public default TypeS evaluationType() {
    return type();
  }

  public TypeS type();
}
