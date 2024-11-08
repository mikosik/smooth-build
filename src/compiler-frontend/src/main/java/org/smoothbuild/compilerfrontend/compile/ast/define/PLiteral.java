package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
import org.smoothbuild.common.log.location.Location;

public abstract sealed class PLiteral extends PExpr permits PBlob, PInt, PString {
  private final String literal;

  public PLiteral(String literal, Location location) {
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
    return object instanceof PLiteral that
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
    var fields = list("literal = " + literal, "location = " + location()).toString("\n");
    return getClass().getName() + "(\n" + indent(fields) + "\n)";
  }
}
