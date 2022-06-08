package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Loc;

/**
 * Annotation.
 */
public final class AnnN extends MonoNamedN {
  private final StringN path;

  public AnnN(String name, StringN path, Loc loc) {
    super(name, loc);
    this.path = path;
  }

  public StringN path() {
    return path;
  }
}
