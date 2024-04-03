package org.smoothbuild.common.dag;

import org.smoothbuild.common.log.base.Try;

public interface Initializable {
  public Try<Void> initialize();
}
