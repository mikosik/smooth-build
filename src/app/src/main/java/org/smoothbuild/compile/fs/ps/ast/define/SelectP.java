package org.smoothbuild.compile.fs.ps.ast.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;

import java.util.Objects;

import org.smoothbuild.compile.fs.lang.base.location.Location;

public final class SelectP extends ExprP {
  private final ExprP selectable;
  private final String field;

  public SelectP(ExprP selectable, String field, Location location) {
    super(location);
    this.selectable = selectable;
    this.field = field;
  }

  public ExprP selectable() {
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
    return object instanceof SelectP that
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
    var fields = joinToString("\n",
        "selectable = " + selectable,
        "field = " + field,
        "location = " + location()
    );
    return "SelectP(\n" + indent(fields) + "\n)";
  }
}
