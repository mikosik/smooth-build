package org.smoothbuild.compilerfrontend.lang.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.HasIdAndLocation;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Annotated value (one that has not a body).
 * This class is immutable.
 */
public final class SAnnotatedValue implements SNamedValue, HasIdAndLocation, HasLocation {
  private final SAnnotation annotation;
  private final SSchema schema;
  private final Id id;
  private final Location location;

  public SAnnotatedValue(SAnnotation annotation, SSchema schema, Id id, Location location) {
    this.annotation = annotation;
    this.schema = schema;
    this.id = id;
    this.location = location;
  }

  public SAnnotation annotation() {
    return annotation;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SAnnotatedValue that
        && this.annotation().equals(that.annotation())
        && this.schema().equals(that.schema())
        && this.id().equals(that.id())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(annotation(), schema(), id(), location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SAnnotatedValue")
        .addField("annotation", annotation)
        .addField("schema", schema())
        .addField("name", id())
        .addField("location", location())
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
