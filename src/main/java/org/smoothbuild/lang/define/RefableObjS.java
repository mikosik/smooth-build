package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Nal;
import org.smoothbuild.lang.like.RefableObj;
import org.smoothbuild.lang.type.TypeS;

/**
 * Top level evaluable.
 */
public sealed interface RefableObjS extends RefableObj, RefableS, Nal
    permits FuncS, ValS {
  public TypeS type();
}
