package org.smoothbuild.common.init;

import static org.smoothbuild.common.log.base.Try.failure;
import static org.smoothbuild.common.log.base.Try.success;

import jakarta.inject.Inject;
import java.util.Set;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.plan.TryFunction0;

public class Initializer implements TryFunction0<Void> {
  private final Set<Initializable> initializables;

  @Inject
  public Initializer(Set<Initializable> initializables) {
    this.initializables = initializables;
  }

  @Override
  public Try<Void> apply() {
    var logger = new Logger();
    for (Initializable initializable : initializables) {
      var result = initializable.initialize();
      logger.logAll(result.logs());
      if (result.toMaybe().isNone()) {
        return failure(logger);
      }
    }
    return success(null, logger);
  }
}
