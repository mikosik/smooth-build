package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.compilerfrontend.lang.define.SNamedValue.valueHeaderToSourceCode;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.IdentifiableCode;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Named Expression Value (one that has a body).
 * This class is immutable.
 */
public final class SNamedExprValue implements SNamedValue, IdentifiableCode {
  private final SExpr body;
  private final SSchema schema;
  private final Fqn fqn;
  private final Location location;

  public SNamedExprValue(SSchema schema, Fqn fqn, SExpr body, Location location) {
    this.schema = schema;
    this.fqn = fqn;
    this.body = body;
    this.location = location;
  }

  public SExpr body() {
    return body;
  }

  @Override
  public String toSourceCode() {
    return valueHeaderToSourceCode(this) + "\n  = " + body.toSourceCode() + ";";
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SNamedExprValue that
        && this.schema().equals(that.schema())
        && this.fqn().equals(that.fqn())
        && this.body().equals(that.body())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(schema(), fqn(), body(), location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SNamedExprValue")
        .addField("schema", schema())
        .addField("fqn", fqn())
        .addField("location", location())
        .addField("body", body)
        .toString();
  }

  @Override
  public SSchema schema() {
    return schema;
  }

  @Override
  public Fqn fqn() {
    return fqn;
  }

  @Override
  public Location location() {
    return location;
  }
}
