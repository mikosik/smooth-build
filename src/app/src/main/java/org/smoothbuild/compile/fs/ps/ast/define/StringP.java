package org.smoothbuild.compile.fs.ps.ast.define;

import org.smoothbuild.common.Strings;
import org.smoothbuild.common.UnescapingFailedExc;
import org.smoothbuild.compile.fs.lang.base.location.Location;

public final class StringP extends LiteralP {
  private String unescaped;

  public StringP(String literal, Location location) {
    super(literal, location);
  }

  public String unescapedValue() {
    return unescaped;
  }

  public void calculateUnescaped() throws UnescapingFailedExc {
    unescaped = Strings.unescaped(literal());
  }
}
