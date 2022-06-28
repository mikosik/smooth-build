package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Loc;

/**
 * Annotation.
 */
public final class AnnP extends MonoNamedP {
  private final StringP path;

  public AnnP(String name, StringP path, Loc loc) {
    super(name, loc);
    this.path = path;
  }

  public StringP path() {
    return path;
  }
}
