package org.smoothbuild.compile.fs.lang.define;

import static org.smoothbuild.common.Strings.indent;

import java.util.Objects;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.SchemaS;

/**
 * Named Expression Value (one that has a body).
 * This class is immutable.
 */
public final class NamedExprValueS extends NamedValueS {
  private final ExprS body;

  public NamedExprValueS(SchemaS schema, String name, ExprS body, Location location) {
    super(schema, name, location);
    this.body = body;
  }

  public ExprS body() {
    return body;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof NamedExprValueS that
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


