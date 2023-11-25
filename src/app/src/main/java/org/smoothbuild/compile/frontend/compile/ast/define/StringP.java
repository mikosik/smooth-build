package org.smoothbuild.compile.frontend.compile.ast.define;

import org.smoothbuild.common.Strings;
import org.smoothbuild.common.UnescapeFailedException;
import org.smoothbuild.compile.frontend.lang.base.location.Location;

public final class StringP extends LiteralP {
  private String unescaped;

  public StringP(String literal, Location location) {
    super(literal, location);
  }

  public String unescapedValue() {
    return unescaped;
  }

  public void calculateUnescaped() throws UnescapeFailedException {
    unescaped = Strings.unescaped(literal());
  }
}