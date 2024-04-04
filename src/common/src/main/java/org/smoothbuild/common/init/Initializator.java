package org.smoothbuild.common.init;

import static org.smoothbuild.common.log.base.Try.success;

import jakarta.inject.Inject;
import java.util.Set;
import org.smoothbuild.common.dag.TryFunction0;
import org.smoothbuild.common.log.base.Try;

public class Initializator implements TryFunction0<Void> {
  private final Set<Initializable> initializables;

  @Inject
  public Initializator(Set<Initializable> initializables) {
    this.initializables = initializables;
  }

  @Override
  public Try<Void> apply() {
    for (Initializable initializable : initializables) {
      var result = initializable.initialize();
      if (result.toMaybe().isNone()) {
        return result;
      }
    }
    return success(null);
  }
}
