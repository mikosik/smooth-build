package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Nal;

/**
 * Named Evaluable.
 */
public sealed interface NamedEvaluableS extends EvaluableS, RefableS, Nal
    permits NamedFuncS, NamedValueS {
}
