package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;

public final class SelectNode extends ExprNode {
  private final ExprNode expr;
  private final String fieldName;

  public SelectNode(ExprNode expr, String fieldName, Location location) {
    super(location);
    this.expr = expr;
    this.fieldName = fieldName;
  }

  public ExprNode expr() {
    return expr;
  }

  public String fieldName() {
    return fieldName;
  }
}
