package org.smoothbuild.parse.ast.expr;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.util.Strings;

public final class NamedArgP extends OperatorP {
  private final String name;
  private final ExprP expr;

  public NamedArgP(String name, ExprP expr, Loc loc) {
    super(loc);
    this.name = name;
    this.expr = expr;
  }

  public String name() {
    return name;
  }

  public String q() {
    return Strings.q(name);
  }

  public ExprP expr() {
    return expr;
  }
}
