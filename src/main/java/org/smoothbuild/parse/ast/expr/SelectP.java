package org.smoothbuild.parse.ast.expr;

import org.smoothbuild.lang.base.Loc;

public final class SelectP extends OperP {
  private final ExprP selectable;
  private final String field;

  public SelectP(ExprP selectable, String field, Loc loc) {
    super(loc);
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
