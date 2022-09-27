package org.smoothbuild.compile.ps.ast.refable;

import java.util.Optional;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.ps.ast.AnnP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.type.TypeP;

public final class NamedValP extends PolyEvaluableP {
  private final Optional<TypeP> type;

  public NamedValP(Optional<TypeP> type, String name, Optional<ExprP> body,
      Optional<AnnP> annotation, Loc loc) {
    super(name, body, annotation, loc);
    this.type = type;
  }

  public Optional<TypeP> type() {
    return type;
  }
}
