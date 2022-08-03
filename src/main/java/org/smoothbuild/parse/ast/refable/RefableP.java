package org.smoothbuild.parse.ast.refable;

import java.util.Optional;

import org.smoothbuild.lang.base.Nal;
import org.smoothbuild.parse.ast.AnnP;
import org.smoothbuild.parse.ast.expr.ExprP;
import org.smoothbuild.parse.ast.type.TypeP;

/**
 * Referencable.
 */
public sealed interface RefableP extends Nal
    permits PolyRefableP, ItemP {

  public Optional<AnnP> ann();

  public Optional<TypeP> evalT();

  public Optional<ExprP> body();
}
