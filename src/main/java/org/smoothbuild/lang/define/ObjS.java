package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Nal;
import org.smoothbuild.lang.type.TypeS;

/**
 * Literal or expression in smooth language.
 */
public sealed interface ObjS extends Nal permits MonoObjS, PolyObjS, RefableS {
  public TypeS type();
}
