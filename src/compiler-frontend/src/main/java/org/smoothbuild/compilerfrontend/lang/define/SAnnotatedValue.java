package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.compilerfrontend.lang.define.SNamedValue.valueHeaderToSourceCode;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.Identifiable;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Annotated value (one that has not a body).
 * This class is immutable.
 */
public final class SAnnotatedValue implements SNamedValue, Identifiable {
  private final SAnnotation annotation;
  private final SSchema schema;
  private final Fqn fqn;
  private final Location location;

  public SAnnotatedValue(SAnnotation annotation, SSchema schema, Fqn fqn, Location location) {
    this.annotation = annotation;
    this.schema = schema;
    this.fqn = fqn;
    this.location = location;
  }

  public SAnnotation annotation() {
    return annotation;
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

  @Override
  public String toSourceCode() {
    return annotation.toSourceCode() + "\n" + valueHeaderToSourceCode(this) + ";";
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SAnnotatedValue that
        && this.annotation().equals(that.annotation())
        && this.schema().equals(that.schema())
        && this.fqn().equals(that.fqn())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(annotation(), schema(), fqn(), location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SAnnotatedValue")
        .addField("annotation", annotation)
        .addField("schema", schema())
        .addField("fqn", fqn())
        .addField("location", location())
        .toString();
  }
}
