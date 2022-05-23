package org.smoothbuild.lang.define;

import org.smoothbuild.lang.like.Eval;
import org.smoothbuild.lang.type.TypeS;

/**
 * Evaluable.
 */
public class EvalS extends DefinedS implements Eval {
  public EvalS(TypeS type, ModPath modPath, String name, Loc loc) {
    super(type, modPath, name, loc);
  }
}
