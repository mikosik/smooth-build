package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;

/**
 * Annotation.
 */
public record AnnS(StringS path, boolean isPure, Location location) {
  @Override
  public String toString() {
    return """
        @Native("%s", %s)""".formatted(path.string(), isPure ? "PURE" : "IMPURE");
  }
}
