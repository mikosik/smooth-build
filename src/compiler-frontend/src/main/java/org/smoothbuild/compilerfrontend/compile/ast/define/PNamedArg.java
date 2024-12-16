package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.HasNameText;

public final class PNamedArg extends PExpr implements HasNameText {
  private final String nameText;
  private final PExpr expr;

  public PNamedArg(String nameText, PExpr expr, Location location) {
    super(location);
    this.nameText = nameText;
    this.expr = expr;
  }

  @Override
  public String nameText() {
    return nameText;
  }

  public String q() {
    return Strings.q(nameText);
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
        && Objects.equals(this.nameText, that.nameText)
        && Objects.equals(this.expr, that.expr)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(nameText, expr, location());
  }

  @Override
  public String toString() {
    var fields =
        list("name = " + nameText, "expr = " + expr, "location = " + location()).toString("\n");
    return "PNamedArg(\n" + indent(fields) + "\n)";
  }
}
