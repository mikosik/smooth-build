package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.define.Loc;

public final class DefaultArgN extends ArgN {
  public DefaultArgN(ExprN expr, Loc loc) {
    super(null, expr, loc);
  }

  @Override
  public String nameSanitized() {
    return "<default>";
  }
}
