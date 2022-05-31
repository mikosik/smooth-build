package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.Patanal;
import org.smoothbuild.lang.like.RefableObj;
import org.smoothbuild.lang.type.TypeS;

/**
 * Top level evaluable.
 */
public sealed abstract class RefableObjS extends Patanal implements RefableObj, RefableS
    permits FuncS, ValS {
  public RefableObjS(TypeS type, ModPath modPath, String name, Loc loc) {
    super(type, modPath, name, loc);
  }
}
