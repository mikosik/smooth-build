package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Check.checkInitializedToNotNull;

import org.jspecify.annotations.Nullable;
import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.base.UnescapeFailedException;
import org.smoothbuild.common.log.location.Location;

public final class PString extends PLiteral {
  private @Nullable String unescaped;

  public PString(String literal, Location location) {
    super(literal, location);
  }

  public String unescapedValue() {
    return checkInitializedToNotNull(unescaped, "unescaped");
  }

  public void calculateUnescaped() throws UnescapeFailedException {
    unescaped = Strings.unescaped(literal());
  }
}
