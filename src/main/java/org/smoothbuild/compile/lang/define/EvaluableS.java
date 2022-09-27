package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.Tal;
import org.smoothbuild.compile.lang.base.WithLoc;
import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Mono Evaluable.
 * @see PolyEvaluableS
 */
public abstract sealed class EvaluableS extends Tal implements ValS, WithLoc
    permits NamedEvaluableS, UnnamedDefValS {

  public EvaluableS(TypeS type, Loc loc) {
    super(type, loc);
  }
}
