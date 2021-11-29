package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;

public final class AnnotationN extends ExprN {
  private final StringN path;
  private final boolean isPure;

  public AnnotationN(StringN path, boolean isPure, Location location) {
    super(location);
    this.path = path;
    this.isPure = isPure;
  }

  public StringN path() {
    return path;
  }

  public boolean isPure() {
    return isPure;
  }
}
