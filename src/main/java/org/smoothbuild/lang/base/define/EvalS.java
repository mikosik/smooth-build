package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.base.like.EvalLike;
import org.smoothbuild.lang.base.type.impl.TypeS;

/**
 * Evaluable.
 */
public class EvalS extends DefinedS implements EvalLike {
  public EvalS(TypeS type, ModPath modPath, String name, Loc loc) {
    super(type, modPath, name, loc);
  }
}
