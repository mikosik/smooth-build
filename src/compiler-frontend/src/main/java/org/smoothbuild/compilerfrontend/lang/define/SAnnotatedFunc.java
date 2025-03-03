package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.collect.Maybe.none;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

/**
 * Annotated function that has no defined body.
 * This class is immutable.
 */
public final class SAnnotatedFunc extends SNamedFunc {
  private final SAnnotation annotation;

  public SAnnotatedFunc(
      SAnnotation annotation, SType resultType, Fqn fqn, NList<SItem> params, Location location) {
    super(resultType, fqn, params, location);
    this.annotation = annotation;
  }

  public SAnnotation annotation() {
    return annotation;
  }

  @Override
  public String toSourceCode() {
    return toSourceCode(none());
  }

  @Override
  public String toSourceCode(Maybe<List<STypeVar>> typeParams) {
    return annotation.toSourceCode() + "\n" + funcHeaderToSourceCode(typeParams) + ";";
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SAnnotatedFunc that
        && this.annotation.equals(that.annotation)
        && this.type().equals(that.type())
        && this.fqn().equals(that.fqn())
        && this.params().equals(that.params())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(annotation, type(), fqn(), params(), location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SAnnotatedFunc")
        .addField("annotation", annotation)
        .addField("type", type())
        .addListField("params", params().list())
        .addField("location", location())
        .toString();
  }
}
