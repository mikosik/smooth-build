package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Nal;

/**
 * Named Mono Evaluable.
 */
public sealed interface NamedEvaluableS extends EvaluableS, Nal
    permits NamedFuncS, ValS {
}
