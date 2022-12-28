package org.smoothbuild.compile.fs.ps.ast.expr;

import org.smoothbuild.compile.fs.lang.base.Nal;

/**
 * Referencable.
 */
public sealed interface RefableP extends Nal
    permits ItemP, NamedEvaluableP {
  public String simpleName();
}
