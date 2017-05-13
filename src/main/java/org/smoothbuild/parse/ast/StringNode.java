package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.message.CodeLocation;

public class StringNode extends ExprNode {
  private final String value;

  public StringNode(String value, CodeLocation codeLocation) {
    super(codeLocation);
    this.value = value;
  }

  public String value() {
    return value;
  }
}
