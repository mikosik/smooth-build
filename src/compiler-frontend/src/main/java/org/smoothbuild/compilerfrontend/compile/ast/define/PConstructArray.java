package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.collect.List.listOfAll;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.location.Location;

public final class PConstructArray extends PExpr {
  private final List<PExpr> elements;

  public PConstructArray(List<PExpr> elements, Location location) {
    super(location);
    this.elements = listOfAll(elements);
  }

  public List<PExpr> elements() {
    return elements;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PConstructArray that
        && Objects.equals(this.elements, that.elements)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(elements, location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("PConstructArray")
        .addField("elems", elements)
        .addField("location", location())
        .toString();
  }
}
