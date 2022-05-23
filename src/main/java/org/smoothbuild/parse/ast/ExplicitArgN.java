package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.define.Loc;

public final class ExplicitArgN extends ArgN {
  public ExplicitArgN(String name, ExprN expr, Loc loc) {
    super(name, expr, loc);
  }

  @Override
  public String nameSanitized() {
    return declaresName() ? name() : "<nameless>";
  }
}
