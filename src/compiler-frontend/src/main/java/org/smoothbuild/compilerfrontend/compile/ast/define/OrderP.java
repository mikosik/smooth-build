package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;

import java.util.Objects;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;

public final class OrderP extends ExprP {
  private final List<ExprP> elements;

  public OrderP(List<ExprP> elements, Location location) {
    super(location);
    this.elements = listOfAll(elements);
  }

  public List<ExprP> elements() {
    return elements;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof OrderP that
        && Objects.equals(this.elements, that.elements)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(elements, location());
  }

  @Override
  public String toString() {
    var fields = list("elems = " + elements, "location = " + location()).toString("\n");
    return "OrderP(\n" + indent(fields) + "\n)";
  }
}
