package org.smoothbuild.parse.ast.expr;

import static org.smoothbuild.lang.type.TypeFS.STRING;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.util.Strings;
import org.smoothbuild.util.UnescapingFailedExc;

public final class StringP extends ConstantP {
  private final String value;
  private String unescaped;

  public StringP(String value, Loc loc) {
    super(STRING, loc);
    this.value = value;
  }

  public String unescapedValue() {
    return unescaped;
  }

  public void calculateUnescaped() throws UnescapingFailedExc {
    unescaped = Strings.unescaped(value);
  }
}
