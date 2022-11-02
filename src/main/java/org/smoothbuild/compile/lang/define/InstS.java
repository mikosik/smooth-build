package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Instance of a value.
 */
public sealed interface InstS extends ExprS permits BlobS, IntS, StringS {
  @Override
  public default TypeS evalT() {
    return type();
  }

  public TypeS type();
}
