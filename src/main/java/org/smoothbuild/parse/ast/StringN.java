package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.util.Strings;
import org.smoothbuild.util.UnescapingFailedExc;

public final class StringN extends CnstN {
  private final String value;
  private String unescaped;

  public StringN(String value, Loc loc) {
    super(loc);
    this.value = value;
  }

  public String unescapedValue() {
    return unescaped;
  }

  public void calculateUnescaped() throws UnescapingFailedExc {
    unescaped = Strings.unescaped(value);
  }
}
