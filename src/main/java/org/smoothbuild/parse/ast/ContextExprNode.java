package org.smoothbuild.parse.ast;

import org.smoothbuild.antlr.SmoothParser.ExprContext;
import org.smoothbuild.lang.message.CodeLocation;

public class ContextExprNode extends ExprNode {
  private final ExprContext expr;

  public ContextExprNode(ExprContext expr, CodeLocation codeLocation) {
    super(codeLocation);
    this.expr = expr;
  }

  public ExprContext expr() {
    return expr;
  }
}
