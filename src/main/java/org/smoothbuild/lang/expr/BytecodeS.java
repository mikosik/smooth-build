package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Loc;

/**
 * Bytecode annotation.
 */
public record BytecodeS(StringS path, Loc loc) {
  @Override
  public String toString() {
    return "@Bytecode(\"%s\")".formatted(path.string());
  }
}
