package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.base.Strings.indent;

import java.util.Objects;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

/**
 * Annotated value (one that has not a body).
 * This class is immutable.
 */
public final class SAnnotatedValue extends SNamedValue {
  private final SAnnotation annotation;

  public SAnnotatedValue(SAnnotation annotation, SchemaS schema, String name, Location location) {
    super(schema, name, location);
    this.annotation = annotation;
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
        && this.name().equals(that.name())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(annotation(), schema(), name(), location());
  }

  @Override
  public String toString() {
    var fieldsString = annotation().toString() + "\n" + fieldsToString();
    return "SAnnotatedValue(\n" + indent(fieldsString) + "\n)";
  }
}
