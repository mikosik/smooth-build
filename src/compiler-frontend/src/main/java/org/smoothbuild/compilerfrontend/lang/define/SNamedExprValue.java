package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.base.Strings.indent;

import java.util.Objects;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

/**
 * Named Expression Value (one that has a body).
 * This class is immutable.
 */
public final class SNamedExprValue extends SNamedValue {
  private final SExpr body;

  public SNamedExprValue(SchemaS schema, String name, SExpr body, Location location) {
    super(schema, name, location);
    this.body = body;
  }

  public SExpr body() {
    return body;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SNamedExprValue that
        && this.schema().equals(that.schema())
        && this.name().equals(that.name())
        && this.body().equals(that.body())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(schema(), name(), body(), location());
  }

  @Override
  public String toString() {
    var fieldsString = fieldsToString() + "\nbody = " + body;
    return "NamedExprValue(\n" + indent(fieldsString) + "\n)";
  }
}
