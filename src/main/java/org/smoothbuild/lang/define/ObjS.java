package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Nal;
import org.smoothbuild.lang.like.Obj;
import org.smoothbuild.lang.type.TKind;

/**
 * Literal or expression in smooth language.
 */
public sealed interface ObjS extends Nal, Obj permits MonoObjS, PolyObjS, RefableObjS {
  public TKind type();
}
