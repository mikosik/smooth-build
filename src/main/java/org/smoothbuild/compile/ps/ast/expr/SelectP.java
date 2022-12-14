package org.smoothbuild.compile.ps.ast.expr;

import org.smoothbuild.compile.lang.base.location.Location;

public final class SelectP extends ExprP {
  private final ExprP selectable;
  private final String field;

  public SelectP(ExprP selectable, String field, Location location) {
    super(location);
    this.selectable = selectable;
    this.field = field;
  }

  public ExprP selectable() {
    return selectable;
  }

  public String field() {
    return field;
  }
}
