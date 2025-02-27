package org.smoothbuild.compilerfrontend.compile.ast.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.DefaultValue;
import org.smoothbuild.compilerfrontend.lang.base.PolyReferenceable;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

public class PDefaultValue implements DefaultValue, HasLocation {
  private final PExpr expr;
  private final Location location;
  private Fqn fqn;
  private PolyReferenceable referenced;

  public PDefaultValue(PExpr expr, Location location) {
    this.expr = expr;
    this.location = location;
  }

  public PExpr expr() {
    return expr;
  }

  @Override
  public Location location() {
    return location;
  }

  @Override
  public Fqn fqn() {
    return fqn;
  }

  public void setFqn(Fqn fqn) {
    this.fqn = fqn;
  }

  public void setReferenced(PolyReferenceable referenced) {
    this.referenced = referenced;
  }

  public PolyReferenceable referenced() {
    return referenced;
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
