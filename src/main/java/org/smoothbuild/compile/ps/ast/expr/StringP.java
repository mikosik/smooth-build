package org.smoothbuild.compile.ps.ast.expr;

import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.util.Strings;
import org.smoothbuild.util.UnescapingFailedExc;

public final class StringP extends ExprP {
  private final String value;
  private String unescaped;

  public StringP(String value, Location location) {
    super(location);
    this.value = value;
  }

  public String unescapedValue() {
    return unescaped;
  }

  public void calculateUnescaped() throws UnescapingFailedExc {
    unescaped = Strings.unescaped(value);
  }
}
