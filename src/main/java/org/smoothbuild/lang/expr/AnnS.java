package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Loc;

/**
 * Annotation.
 */
public record AnnS(StringS path, boolean isPure, Loc loc) {
  @Override
  public String toString() {
    return """
        @Native("%s", %s)""".formatted(path.string(), isPure ? "PURE" : "IMPURE");
  }
}
