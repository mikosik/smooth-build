package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.like.Expr;

public final class DefaultArgN extends ArgN {
  public DefaultArgN(Expr expr, Loc loc) {
    super(null, expr, loc);
    setType(expr.typeO());
  }

  @Override
  public String nameSanitized() {
    return "<default>";
  }
}
