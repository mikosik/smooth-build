package org.smoothbuild.common.testing;

import java.time.Duration;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionFactory;

public class AwaitHelper {
  public static ConditionFactory await() {
    return Awaitility.await().pollInterval(Duration.ofMillis(5));
  }
}
