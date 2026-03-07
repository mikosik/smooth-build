package org.smoothbuild.compilerfrontend.compile.ast.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;

public final class PTupleGet extends PExpr {
  private final PExpr tupleExpr;
  private final PPosition position;

  public PTupleGet(PExpr tupleExpr, PPosition position, Location location) {
    super(location);
    this.tupleExpr = tupleExpr;
    this.position = position;
  }

  public PExpr tupleExpr() {
    return tupleExpr;
  }

  public PPosition position() {
    return position;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PTupleGet that
        && Objects.equals(this.tupleExpr, that.tupleExpr)
        && Objects.equals(this.position, that.position)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(tupleExpr, position, location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("PTupleGet")
        .addField("tupleExpr", tupleExpr)
        .addField("index", position)
        .addField("location", location())
        .toString();
  }
}
