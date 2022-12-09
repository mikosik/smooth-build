package org.smoothbuild.compile.ps.ast.expr;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.NalImpl;

import com.google.common.base.Objects;

/**
 * Annotation.
 */
public final class AnnP extends NalImpl {
  private final StringP path;

  public AnnP(String name, StringP path, Loc loc) {
    super(name, loc);
    this.path = path;
  }

  public StringP path() {
    return path;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof AnnP that
        && this.name().equals(that.name())
        && this.path().equals(that.path())
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.name(), this.path, this.loc());
  }
}
