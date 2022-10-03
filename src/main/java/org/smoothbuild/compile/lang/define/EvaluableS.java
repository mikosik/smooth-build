package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.WithLoc;

/**
 * Mono Evaluable.
 * @see PolyEvaluableS
 */
public abstract sealed interface EvaluableS extends InstS, WithLoc
    permits NamedEvaluableS, UnnamedDefValS {
}
