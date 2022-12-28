package org.smoothbuild.compile.fs.lang.define;

import org.smoothbuild.compile.fs.lang.base.Nal;

/**
 * Evaluable that has fully qualified name.
 */
public sealed interface NamedEvaluableS extends EvaluableS, RefableS, Nal
    permits NamedFuncS, NamedValueS {
}
