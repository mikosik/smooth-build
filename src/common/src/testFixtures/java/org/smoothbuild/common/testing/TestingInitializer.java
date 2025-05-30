package org.smoothbuild.common.testing;

import static org.smoothbuild.common.testing.AwaitHelper.await;

import org.smoothbuild.common.dagger.CommonTestComponent;

public class TestingInitializer {
  public static void runInitializer(CommonTestComponent component) {
    var promise = component.scheduler().submit(component.initializer());
    await().until(() -> promise.toMaybe().isSome());
  }
}
