package org.smoothbuild.compile.frontend.compile.ast.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.Iterables.joinToString;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import org.smoothbuild.compile.frontend.lang.base.location.Location;

public final class OrderP extends ExprP {
  private final List<ExprP> elems;

  public OrderP(List<ExprP> elems, Location location) {
    super(location);
    this.elems = ImmutableList.copyOf(elems);
  }

  public List<ExprP> elems() {
    return elems;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof OrderP that
        && Objects.equals(this.elems, that.elems)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(elems, location());
  }

  @Override
  public String toString() {
    var fields = joinToString("\n", "elems = " + elems, "location = " + location());
    return "OrderP(\n" + indent(fields) + "\n)";
  }
}
