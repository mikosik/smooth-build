package org.smoothbuild.compile.fs.lang.define;

import org.smoothbuild.compile.fs.lang.type.TypeS;

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
