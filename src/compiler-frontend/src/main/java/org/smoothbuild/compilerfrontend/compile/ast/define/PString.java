package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.base.UnescapeFailedException;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;

public final class PString extends PLiteral {
  private String unescaped;

  public PString(String literal, Location location) {
    super(literal, location);
  }

  public String unescapedValue() {
    return unescaped;
  }

  public void calculateUnescaped() throws UnescapeFailedException {
    unescaped = Strings.unescaped(literal());
  }
}
