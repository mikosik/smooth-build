package org.smoothbuild.parse.ast;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.lang.message.CodeLocation;

public class ArgNode extends Node {
  private final int number;
  private final String name;
  private final ExprNode expr;

  public ArgNode(int number, String name, ExprNode expr, CodeLocation codeLocation) {
    super(codeLocation);
    this.number = number;
    this.name = name;
    this.expr = expr;
  }

  public int number() {
    return number;
  }

  public boolean hasName() {
    return name != null;
  }

  public String name() {
    checkState(hasName());
    return name;
  }

  public ExprNode expr() {
    return expr;
  }
}
