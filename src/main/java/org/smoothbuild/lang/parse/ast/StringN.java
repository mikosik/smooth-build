package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.util.Strings;
import org.smoothbuild.util.UnescapingFailedException;

public final class StringN extends ExprN {
  private final String value;
  private String unescaped;

  public StringN(String value, Location location) {
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
