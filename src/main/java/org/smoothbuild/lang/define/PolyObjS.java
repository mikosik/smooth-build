package org.smoothbuild.lang.define;

import org.smoothbuild.lang.type.PolyTS;

/**
 * Polymorphic object.
 */
public sealed interface PolyObjS extends ObjS
    permits PolyExprS, PolyTopRefableS {

  @Override
  public PolyTS type();
}
