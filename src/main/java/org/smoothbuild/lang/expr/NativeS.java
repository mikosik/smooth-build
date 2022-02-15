package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Loc;

/**
 * Native annotation.
 */
public record NativeS(StringS path, boolean isPure, Loc loc) {
  @Override
  public String toString() {
    return ("@Native" + (isPure ? "" : "Impure") + "(\"%s\")").formatted(path.string());
  }
}
