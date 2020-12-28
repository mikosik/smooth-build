package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.type.Types.any;
import static org.smoothbuild.lang.base.type.Types.nothing;

public enum Side {
  UPPER,
  LOWER;

  public Side reversed() {
    return switch (this) {
      case UPPER -> LOWER;
      case LOWER -> UPPER;
    };
  }

  public Type edge() {
    return switch (this) {
      case UPPER -> any();
      case LOWER -> nothing();
    };
  }
}
