package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.WithLoc;
import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Mono Evaluable.
 * @see PolyEvaluableS
 */
public abstract sealed interface EvaluableS extends WithLoc
    permits FuncS, NamedEvaluableS {
  public TypeS type();
}
