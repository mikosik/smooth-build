package org.smoothbuild.compile.ps.ast.expr;

import org.smoothbuild.compile.lang.base.NalImpl;
import org.smoothbuild.compile.lang.base.location.Location;

import com.google.common.base.Objects;

/**
 * Annotation.
 */
public final class AnnotationP extends NalImpl {
  private final StringP value;

  public AnnotationP(String name, StringP value, Location location) {
    super(name, location);
    this.value = value;
  }

  public StringP value() {
    return value;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof AnnotationP that
        && this.name().equals(that.name())
        && this.value().equals(that.value())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.name(), this.value, this.location());
  }
}
