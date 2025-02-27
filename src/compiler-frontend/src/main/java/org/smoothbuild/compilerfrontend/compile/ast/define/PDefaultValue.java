package org.smoothbuild.compilerfrontend.compile.ast.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.compilerfrontend.lang.base.DefaultValue;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

public class PDefaultValue implements DefaultValue {
  private final PExpr expr;
  private Fqn fqn;

  public PDefaultValue(PExpr expr) {
    this.expr = expr;
  }

  public PExpr expr() {
    return expr;
  }

  @Override
  public Fqn fqn() {
    return fqn;
  }

  public void setFqn(Fqn fqn) {
    this.fqn = fqn;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof PDefaultValue that
        && Objects.equals(this.expr, that.expr)
        && Objects.equals(this.fqn, that.fqn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(expr, fqn);
  }

  @Override
  public String toString() {
    return new ToStringBuilder("PDefaultValue")
        .addField("expr", expr)
        .addField("fqn", fqn())
        .toString();
  }
}
