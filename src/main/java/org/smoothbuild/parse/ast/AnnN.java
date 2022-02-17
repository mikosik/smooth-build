package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.define.Loc;

/**
 * Annotation.
 */
public final class AnnN extends NamedN {
  private final StringN path;

  public AnnN(String name, StringN path, Loc loc) {
    super(name, loc);
    this.path = path;
  }

  public StringN path() {
    return path;
  }
}
