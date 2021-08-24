package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.type.Types.anyT;
import static org.smoothbuild.lang.base.type.Types.nothingT;

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
      case UPPER -> anyT();
      case LOWER -> nothingT();
    };
  }
}
