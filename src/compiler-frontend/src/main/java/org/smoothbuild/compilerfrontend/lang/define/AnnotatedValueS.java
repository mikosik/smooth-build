package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.Strings.indent;

import java.util.Objects;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

/**
 * Annotated value (one that has not a body).
 * This class is immutable.
 */
public final class AnnotatedValueS extends NamedValueS {
  private final AnnotationS annotation;

  public AnnotatedValueS(AnnotationS annotation, SchemaS schema, String name, Location location) {
    super(schema, name, location);
    this.annotation = annotation;
  }

  public AnnotationS annotation() {
    return annotation;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof AnnotatedValueS that
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
    return "AnnotatedValue(\n" + indent(fieldsString) + "\n)";
  }
}
