package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Loc;

/**
 * Annotation.
 */
public final class AnnN extends ExprN {
  private final StringN path;
  private final boolean isPure;

  public AnnN(StringN path, boolean isPure, Loc loc) {
    super(loc);
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
