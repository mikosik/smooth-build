package org.smoothbuild.lang.define;

import org.smoothbuild.lang.like.EvalLike;
import org.smoothbuild.lang.type.impl.TypeS;

/**
 * Evaluable.
 */
public class EvalS extends DefinedS implements EvalLike {
  public EvalS(TypeS type, ModPath modPath, String name, Loc loc) {
    super(type, modPath, name, loc);
  }
}
