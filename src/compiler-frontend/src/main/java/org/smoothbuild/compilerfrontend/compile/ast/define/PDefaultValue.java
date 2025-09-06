package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Check.checkInitializedToNotNull;

import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.DefaultValue;
import org.smoothbuild.compilerfrontend.lang.base.PolyEvaluable;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

public class PDefaultValue implements DefaultValue, HasLocation {
  private final PExpr expr;
  private final Location location;
  private @Nullable Fqn fqn;
  private @Nullable PolyEvaluable referenced;

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
    return checkInitializedToNotNull(fqn, "fqn");
  }

  public void setFqn(Fqn fqn) {
    this.fqn = fqn;
  }

  public void setReferenced(PolyEvaluable referenced) {
    this.referenced = referenced;
  }

  public PolyEvaluable referenced() {
    return checkInitializedToNotNull(referenced, "referenced");
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
        .addField("fqn", fqn)
        .toString();
  }
}
