package org.smoothbuild.compile.ps.ast.expr;

import org.smoothbuild.compile.lang.base.Nal;

/**
 * Referencable.
 */
public sealed interface RefableP extends Nal
    permits ItemP, NamedEvaluableP {
}
