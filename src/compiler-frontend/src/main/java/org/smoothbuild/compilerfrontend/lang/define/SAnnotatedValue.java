package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.compilerfrontend.lang.define.SNamedValue.valueHeaderToSourceCode;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.IdentifiableCode;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

/**
 * Annotated value (one that has not a body).
 * This class is immutable.
 */
public final class SAnnotatedValue implements SNamedValue, IdentifiableCode {
  private final SAnnotation annotation;
  private final SType type;
  private final Fqn fqn;
  private final Location location;

  public SAnnotatedValue(SAnnotation annotation, SType type, Fqn fqn, Location location) {
    this.annotation = annotation;
    this.type = type;
    this.fqn = fqn;
    this.location = location;
  }

  public SAnnotation annotation() {
    return annotation;
  }

  @Override
  public SType type() {
    return type;
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
    return toSourceCode(none());
  }

  @Override
  public String toSourceCode(Maybe<List<STypeVar>> typeParams) {
    return annotation.toSourceCode() + "\n" + valueHeaderToSourceCode(this, typeParams) + ";";
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SAnnotatedValue that
        && this.annotation.equals(that.annotation)
        && this.type.equals(that.type)
        && this.fqn.equals(that.fqn)
        && this.location.equals(that.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(annotation, type, fqn, location);
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SAnnotatedValue")
        .addField("annotation", annotation)
        .addField("type", type)
        .addField("fqn", fqn)
        .addField("location", location)
        .toString();
  }
}
