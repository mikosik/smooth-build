package org.smoothbuild.compile.frontend.compile.ast.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.Iterables.joinToString;

import java.util.Objects;
import org.smoothbuild.common.Strings;
import org.smoothbuild.compile.frontend.lang.base.location.Location;

public final class NamedArgP extends ExprP {
  private final String name;
  private final ExprP expr;

  public NamedArgP(String name, ExprP expr, Location location) {
    super(location);
    this.name = name;
    this.expr = expr;
  }

  public String name() {
    return name;
  }

  public String q() {
    return Strings.q(name);
  }

  public ExprP expr() {
    return expr;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof NamedArgP that
        && Objects.equals(this.name, that.name)
        && Objects.equals(this.expr, that.expr)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, expr, location());
  }

  @Override
  public String toString() {
    var fields = joinToString("\n", "name = " + name, "expr = " + expr, "location = " + location());
    return "NamedArgP(\n" + indent(fields) + "\n)";
  }
}
