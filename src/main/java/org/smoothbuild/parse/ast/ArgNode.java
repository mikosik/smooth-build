package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.message.CodeLocation;

public class ArgNode extends Node {
  private final int number;
  private final String name;
  private final ExprNode expr;

  public ArgNode(int number, String name, ExprNode result, CodeLocation codeLocation) {
    super(codeLocation);
    this.number = number;
    this.name = name;
    this.expr = result;
  }

  public int number() {
    return number;
  }

  public String name() {
    return name;
  }

  public ExprNode expr() {
    return expr;
  }
}
