package org.smoothbuild.compile.ps.ast.refable;

import org.smoothbuild.compile.lang.base.Nal;

/**
 * Referencable.
 */
public sealed interface RefableP extends Nal
    permits EvaluableP, ItemP {
}
