package org.smoothbuild.common.testing;

import static org.smoothbuild.common.testing.AwaitHelper.await;

import com.google.inject.Injector;
import org.smoothbuild.common.init.Initializer;
import org.smoothbuild.common.schedule.Scheduler;

public class TestingInitializer {
  public static void runInitializations(Injector injector) {
    var scheduler = injector.getInstance(Scheduler.class);
    var initializer = scheduler.submit(injector.getInstance(Initializer.class));
    await().until(() -> initializer.toMaybe().isSome());
  }
}
