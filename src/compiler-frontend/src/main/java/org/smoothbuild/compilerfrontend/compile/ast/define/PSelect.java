package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
import org.smoothbuild.common.log.location.Location;

public final class PSelect extends PExpr {
  private final PExpr selectable;
  private final String field;

  public PSelect(PExpr selectable, String field, Location location) {
    super(location);
    this.selectable = selectable;
    this.field = field;
  }

  public PExpr selectable() {
    return selectable;
  }

  public String field() {
    return field;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PSelect that
        && Objects.equals(this.selectable, that.selectable)
        && Objects.equals(this.field, that.field)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(selectable, field, location());
  }

  @Override
  public String toString() {
    var fields = list("selectable = " + selectable, "field = " + field, "location = " + location())
        .toString("\n");
    return "SelectP(\n" + indent(fields) + "\n)";
  }
}
