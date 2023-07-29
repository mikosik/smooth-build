package org.smoothbuild.compile.fs.ps.ast.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.Iterables.joinToString;

import java.util.Objects;

import org.smoothbuild.compile.fs.lang.base.location.Location;

public sealed abstract class LiteralP extends ExprP permits BlobP, IntP, StringP {
  private final String literal;
  public LiteralP(String literal, Location location) {
    super(location);
    this.literal = literal;
  }

  public String literal() {
    return literal;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof LiteralP that
        && this.getClass().equals(that.getClass())
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
    return getClass().getName() + "(\n" + indent(fields) + "\n)";
  }
}
