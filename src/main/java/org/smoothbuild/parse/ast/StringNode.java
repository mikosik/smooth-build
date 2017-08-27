package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.message.Location;

public class StringNode extends ExprNode {
  private final String value;

  public StringNode(String value, Location location) {
    super(location);
    this.value = value;
  }

  public String value() {
    return value;
  }
}
