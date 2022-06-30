package org.smoothbuild.lang.define;

import org.smoothbuild.lang.type.MonoTS;

/**
 * Monomorphic object.
 */
public sealed interface MonoObjS extends ObjS permits MonoTopRefableS, CnstS, MonoExprS {
  @Override
  public MonoTS type();
}
