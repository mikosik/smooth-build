package org.smoothbuild.compile.ps.ast.expr;

import org.smoothbuild.compile.lang.base.NalImpl;
import org.smoothbuild.compile.lang.base.location.Location;

import com.google.common.base.Objects;

/**
 * Annotation.
 */
public final class AnnotationP extends NalImpl {
  private final StringP path;

  public AnnotationP(String name, StringP path, Location location) {
    super(name, location);
    this.path = path;
  }

  public StringP path() {
    return path;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof AnnotationP that
        && this.name().equals(that.name())
        && this.path().equals(that.path())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.name(), this.path, this.location());
  }
}
