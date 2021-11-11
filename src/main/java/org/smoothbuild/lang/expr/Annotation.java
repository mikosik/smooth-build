package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;

public record Annotation(StringS path, boolean isPure, Location location) {
  @Override
  public String toString() {
    return """
        @Native("%s", %s)""".formatted(path.string(), isPure ? "PURE" : "IMPURE");
  }
}
