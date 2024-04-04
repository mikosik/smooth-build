package org.smoothbuild.common.init;

import org.smoothbuild.common.log.base.Try;

public interface Initializable {
  public Try<Void> initialize();
}
