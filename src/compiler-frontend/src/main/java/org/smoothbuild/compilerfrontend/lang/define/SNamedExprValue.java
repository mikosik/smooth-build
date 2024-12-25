package org.smoothbuild.compilerfrontend.lang.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.HasIdAndLocation;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Named Expression Value (one that has a body).
 * This class is immutable.
 */
public final class SNamedExprValue implements SNamedValue, HasIdAndLocation, HasLocation {
  private final SExpr body;
  private final SSchema schema;
  private final Id id;
  private final Location location;

  public SNamedExprValue(SSchema schema, Id id, SExpr body, Location location) {
    this.schema = schema;
    this.id = id;
    this.body = body;
    this.location = location;
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
    return new ToStringBuilder("SNamedExprValue")
        .addField("schema", schema())
        .addField("name", id())
        .addField("location", location())
        .addField("body", body)
        .toString();
  }

  @Override
  public SSchema schema() {
    return schema;
  }

  @Override
  public Id id() {
    return id;
  }

  @Override
  public Location location() {
    return location;
  }
}
