package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.Tapanal;
import org.smoothbuild.lang.type.MonoTS;

/**
 * This class is immutable.
 */
public sealed abstract class ValS extends Tapanal implements MonoRefableObjS
    permits AnnValS, DefValS {
  public ValS(MonoTS type, ModPath modPath, String name, Loc loc) {
    super(type, modPath, name, loc);
  }
}


