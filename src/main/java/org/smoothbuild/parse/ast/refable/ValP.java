package org.smoothbuild.parse.ast.refable;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.parse.ast.AnnP;
import org.smoothbuild.parse.ast.expr.ExprP;
import org.smoothbuild.parse.ast.type.TypeP;

public final class ValP extends PolyRefableP {
  private final Optional<TypeP> type;

  public ValP(Optional<TypeP> type, String name, Optional<ExprP> body,
      Optional<AnnP> annotation, Loc loc) {
    super(name, body, annotation, loc);
    this.type = type;
  }

  public Optional<TypeP> type() {
    return type;
  }

  @Override
  public Optional<TypeP> evalT() {
    return type();
  }
}
