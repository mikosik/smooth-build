package org.smoothbuild.lang.define;

import org.smoothbuild.lang.expr.StringS;

/**
 * Annotation.
 */
public record AnnS(String name, StringS path, Loc loc) {
  @Override
  public String toString() {
    return "@%s(\"%s\")".formatted(name, path.string());
  }
}
