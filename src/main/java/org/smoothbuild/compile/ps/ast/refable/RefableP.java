package org.smoothbuild.compile.ps.ast.refable;

import java.util.Optional;

import org.smoothbuild.compile.lang.base.Nal;
import org.smoothbuild.compile.ps.ast.expr.ExprP;

/**
 * Referencable.
 */
public sealed interface RefableP extends Nal
    permits PolyEvaluableP, ItemP {

  public Optional<ExprP> body();
}
