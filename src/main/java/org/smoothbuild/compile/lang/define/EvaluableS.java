package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Nal;

/**
 * Mono Evaluable.
 * @see PolyEvaluableS
 */
public abstract sealed interface EvaluableS extends Nal
    permits FuncS, ValS {
}
