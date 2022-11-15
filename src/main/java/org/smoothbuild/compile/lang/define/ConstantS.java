package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Smooth constant.
 */
public sealed interface ConstantS extends ExprS permits BlobS, IntS, StringS {
  @Override
  public default TypeS evalT() {
    return type();
  }

  public TypeS type();
}
