package org.smoothbuild.parse.ast;

import org.smoothbuild.antlr.SmoothParser.ExpressionContext;
import org.smoothbuild.lang.message.CodeLocation;

public class ContextExprNode extends ExprNode {
  private final ExpressionContext expr;

  public ContextExprNode(ExpressionContext expr, CodeLocation codeLocation) {
    super(codeLocation);
    this.expr = expr;
  }

  public ExpressionContext expr() {
    return expr;
  }
}
