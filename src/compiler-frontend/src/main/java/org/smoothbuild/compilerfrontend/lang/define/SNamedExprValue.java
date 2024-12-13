package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.base.Strings.indent;

import java.util.Objects;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.Id;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Named Expression Value (one that has a body).
 * This class is immutable.
 */
public final class SNamedExprValue extends SNamedValue {
  private final SExpr body;

  public SNamedExprValue(SSchema schema, Id id, SExpr body, Location location) {
    super(schema, id, location);
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
        && this.id().equals(that.id())
        && this.body().equals(that.body())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(schema(), id(), body(), location());
  }

  @Override
  public String toString() {
    var fieldsString = fieldsToString() + "\nbody = " + body;
    return "SNamedExprValue(\n" + indent(fieldsString) + "\n)";
  }
}
