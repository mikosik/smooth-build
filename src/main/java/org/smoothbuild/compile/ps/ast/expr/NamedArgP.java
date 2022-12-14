package org.smoothbuild.compile.ps.ast.expr;

import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.util.Strings;

public final class NamedArgP extends ExprP {
  private final String name;
  private final ExprP expr;

  public NamedArgP(String name, ExprP expr, Location location) {
    super(location);
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
