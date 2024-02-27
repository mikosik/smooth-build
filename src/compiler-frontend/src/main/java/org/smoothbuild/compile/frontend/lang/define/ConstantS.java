package org.smoothbuild.compile.frontend.lang.define;

import org.smoothbuild.compile.frontend.lang.type.TypeS;

/**
 * Smooth constant.
 */
public sealed interface ConstantS extends ExprS permits BlobS, IntS, StringS {
  @Override
  public default TypeS evaluationT() {
    return type();
  }

  public TypeS type();
}
