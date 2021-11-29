package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Loc;

public final class SelectN extends ExprN {
  private final ExprN expr;
  private final String fieldName;

  public SelectN(ExprN expr, String fieldName, Loc loc) {
    super(loc);
    this.expr = expr;
    this.fieldName = fieldName;
  }

  public ExprN expr() {
    return expr;
  }

  public String fieldName() {
    return fieldName;
  }
}
