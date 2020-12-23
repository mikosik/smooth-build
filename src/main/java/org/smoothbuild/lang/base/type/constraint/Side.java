package org.smoothbuild.lang.base.type.constraint;

import static org.smoothbuild.lang.base.type.Types.any;
import static org.smoothbuild.lang.base.type.Types.nothing;

import org.smoothbuild.lang.base.type.Type;

public enum Side {
  UPPER,
  LOWER;

  public Side reversed() {
    return this == UPPER ? LOWER : UPPER;
  }

  public Type edge() {
    return this == UPPER ? any() : nothing();
  }
}
