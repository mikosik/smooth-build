package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.Location;

public class RefNode extends ExprNode {
  private final String name;
  private final boolean hasParentheses;

  public RefNode(String name, boolean hasParentheses, Location location) {
    super(location);
    this.name = name;
    this.hasParentheses = hasParentheses;
  }

  public String name() {
    return name;
  }

  public boolean hasParentheses() {
    return hasParentheses;
  }
}
