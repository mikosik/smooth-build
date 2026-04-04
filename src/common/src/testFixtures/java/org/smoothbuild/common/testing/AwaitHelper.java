package org.smoothbuild.common.testing;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Duration;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionFactory;

public class AwaitHelper {
  public static ConditionFactory await() {
    return Awaitility.await().atMost(30, SECONDS).pollInterval(Duration.ofMillis(5));
  }
}
