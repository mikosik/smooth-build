package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.base.UnescapeFailedException;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;

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
