package org.smoothbuild.compilerfrontend.compile.ast.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;

public final class PTupleSelect extends PExpr {
  private final PExpr selectable;
  private final PPosition position;

  public PTupleSelect(PExpr selectable, PPosition position, Location location) {
    super(location);
    this.selectable = selectable;
    this.position = position;
  }

  public PExpr selectable() {
    return selectable;
  }

  public PPosition position() {
    return position;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PTupleSelect that
        && Objects.equals(this.selectable, that.selectable)
        && Objects.equals(this.position, that.position)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(selectable, position, location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("PSelect")
        .addField("selectable", selectable)
        .addField("index", position)
        .addField("location", location())
        .toString();
  }
}
