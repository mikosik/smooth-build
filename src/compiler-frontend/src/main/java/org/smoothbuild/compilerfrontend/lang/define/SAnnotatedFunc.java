package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.base.Strings.indent;

import java.util.Objects;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;

/**
 * Annotated function that has no defined body.
 * This class is immutable.
 */
public final class SAnnotatedFunc extends SNamedFunc {
  private final SAnnotation annotation;

  public SAnnotatedFunc(
      SAnnotation annotation,
      SFuncSchema schema,
      String name,
      NList<SItem> params,
      Location location) {
    super(schema, name, params, location);
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
    return object instanceof SAnnotatedFunc that
        && this.annotation.equals(that.annotation)
        && this.schema().equals(that.schema())
        && this.name().equals(that.name())
        && this.params().equals(that.params())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(annotation, schema(), name(), params(), location());
  }

  @Override
  public String toString() {
    var fields = annotation.toString() + "\n" + fieldsToString();
    return "SAnnotatedFunc(\n" + indent(fields) + "\n)";
  }
}
