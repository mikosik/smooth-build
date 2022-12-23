package org.smoothbuild.compile.ps.ast.expr;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.util.Strings;
import org.smoothbuild.util.UnescapingFailedExc;

public final class StringP extends ExprP {
  private final String literal;
  private String unescaped;

  public StringP(String literal, Location location) {
    super(location);
    this.literal = literal;
  }

  public String unescapedValue() {
    return unescaped;
  }

  public void calculateUnescaped() throws UnescapingFailedExc {
    unescaped = Strings.unescaped(literal);
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "literal = " + literal,
        "location = " + location()
    );
    return "StringP(\n" + indent(fields) + "\n)";
  }
}
