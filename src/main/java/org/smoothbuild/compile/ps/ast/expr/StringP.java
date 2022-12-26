package org.smoothbuild.compile.ps.ast.expr;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;

import java.util.Objects;

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
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof StringP that
        && Objects.equals(this.literal, that.literal)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(literal, location());
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
