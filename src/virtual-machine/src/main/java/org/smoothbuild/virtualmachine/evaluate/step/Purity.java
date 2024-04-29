package org.smoothbuild.virtualmachine.evaluate.step;

import static org.smoothbuild.common.log.base.ResultSource.DISK;
import static org.smoothbuild.common.log.base.ResultSource.MEMORY;

import org.smoothbuild.common.log.base.ResultSource;

public enum Purity {
  PURE,
  IMPURE,
  ;

  public ResultSource cacheLevel() {
    return switch (this) {
      case PURE -> DISK;
      case IMPURE -> MEMORY;
    };
  }
}
