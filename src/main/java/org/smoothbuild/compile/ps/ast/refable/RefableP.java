package org.smoothbuild.compile.ps.ast.refable;

import java.util.Optional;

import org.smoothbuild.compile.lang.base.Nal;
import org.smoothbuild.compile.ps.ast.AnnP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.type.TypeP;

/**
 * Referencable.
 */
public sealed interface RefableP extends Nal
    permits PolyEvaluableP, ItemP {

  public Optional<AnnP> ann();

  public Optional<TypeP> evalT();

  public Optional<ExprP> body();
}
