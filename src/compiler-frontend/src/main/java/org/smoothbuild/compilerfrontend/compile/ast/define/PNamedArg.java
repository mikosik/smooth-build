package org.smoothbuild.compilerfrontend.compile.ast.define;

import java.util.Objects;
import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Name;

public final class PNamedArg extends PExpr {
  private final String nameText;
  private final PExpr expr;
  private Name name;

  public PNamedArg(String nameText, PExpr expr, Location location) {
    super(location);
    this.nameText = nameText;
    this.expr = expr;
  }

  public String nameText() {
    return nameText;
  }

  public Name name() {
    return name;
  }

  public void setName(Name name) {
    this.name = name;
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
    return new ToStringBuilder("PNamedArg")
        .addField("name", nameText)
        .addField("expr", expr)
        .addField("location", location())
        .toString();
  }
}
