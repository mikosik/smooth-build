package org.smoothbuild.compilerfrontend.lang.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;

/**
 * Annotated function that has no defined body.
 * This class is immutable.
 */
public final class SAnnotatedFunc extends SNamedFunc {
  private final SAnnotation annotation;

  public SAnnotatedFunc(
      SAnnotation annotation, SFuncSchema schema, Fqn fqn, NList<SItem> params, Location location) {
    super(schema, fqn, params, location);
    this.annotation = annotation;
  }

  public SAnnotation annotation() {
    return annotation;
  }

  @Override
  public String toSourceCode() {
    return annotation.toSourceCode() + "\n"
        + funcHeaderToSourceCode(schema().typeParams().toSet()) + ";";
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SAnnotatedFunc that
        && this.annotation.equals(that.annotation)
        && this.schema().equals(that.schema())
        && this.fqn().equals(that.fqn())
        && this.params().equals(that.params())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(annotation, schema(), fqn(), params(), location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SAnnotatedFunc")
        .addField("annotation", annotation)
        .addField("schema", schema())
        .addListField("params", params().list())
        .addField("location", location())
        .toString();
  }
}
