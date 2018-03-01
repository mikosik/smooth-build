package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.message.Location;

public class AccessorNode extends ExprNode {
  private final ExprNode expr;
  private final String fieldName;

  public AccessorNode(ExprNode expr, String fieldName, Location location) {
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
