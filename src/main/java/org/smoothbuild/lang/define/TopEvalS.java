package org.smoothbuild.lang.define;

import org.smoothbuild.lang.type.impl.TypeS;

/**
 * Top level evaluable.
 */
public sealed abstract class TopEvalS extends EvalS implements Nal permits FuncS, ValS {
  public TopEvalS(TypeS type, ModPath modPath, String name, Loc loc) {
    super(type, modPath, name, loc);
  }
}
