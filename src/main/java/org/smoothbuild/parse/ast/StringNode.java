package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.util.Strings;
import org.smoothbuild.util.UnescapingFailedException;

public class StringNode extends ExprNode {
  private final String value;
  private String unescaped;

  public StringNode(String value, Location location) {
    super(location);
    this.value = value;
  }

  public String value() {
    return value;
  }

  public String unescapedValue() {
    return unescaped;
  }

  public void calculateUnescaped() throws UnescapingFailedException {
    unescaped = Strings.unescaped(value);
  }
}
