package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Nal;

/**
 * Evaluable that has fully qualified name.
 */
public sealed interface NamedEvaluableS extends EvaluableS, RefableS, Nal
    permits NamedFuncS, NamedValueS {
}
