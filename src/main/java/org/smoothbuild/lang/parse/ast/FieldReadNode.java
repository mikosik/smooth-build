package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;

public class FieldReadNode extends ExprNode {
  private final ExprNode expr;
  private final String fieldName;

  public FieldReadNode(ExprNode expr, String fieldName, Location location) {
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
