package org.smoothbuild.virtualmachine.evaluate.step;

import static org.smoothbuild.common.log.base.Origin.DISK;
import static org.smoothbuild.common.log.base.Origin.MEMORY;

import org.smoothbuild.common.log.base.Origin;

public enum Purity {
  PURE,
  IMPURE,
  ;

  public Origin cacheLevel() {
    return switch (this) {
      case PURE -> DISK;
      case IMPURE -> MEMORY;
    };
  }
}
