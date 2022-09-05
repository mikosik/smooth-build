package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;

/**
 * Annotation.
 */
public record AnnS(String name, StringS path, Loc loc) {
  @Override
  public String toString() {
    return "@%s(\"%s\") loc=%s".formatted(name, path, loc);
  }
}
