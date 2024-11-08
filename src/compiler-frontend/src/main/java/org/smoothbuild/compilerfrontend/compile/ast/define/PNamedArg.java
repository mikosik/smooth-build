package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.log.location.Location;

public final class PNamedArg extends PExpr {
  private final String name;
  private final PExpr expr;

  public PNamedArg(String name, PExpr expr, Location location) {
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

  public PExpr expr() {
    return expr;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PNamedArg that
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
    var fields =
        list("name = " + name, "expr = " + expr, "location = " + location()).toString("\n");
    return "NamedArgP(\n" + indent(fields) + "\n)";
  }
}
