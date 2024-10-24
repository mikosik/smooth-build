package org.smoothbuild.common.init;

import static com.google.common.truth.Truth.assertThat;
import static org.awaitility.Awaitility.await;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.task.Output.output;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.testing.TestingCommon;

public class InitializerTest extends TestingCommon {
  @Test
  void initializer_schedules_all_initializables() {
    var visited1 = new AtomicBoolean(false);
    var visited2 = new AtomicBoolean(false);
    var initializable1 = initializable(visited1);
    var initializable2 = initializable(visited2);
    var scheduler = scheduler();

    var initializer = new Initializer(Set.of(initializable1, initializable2), scheduler);
    var result = scheduler.submit(initializer);
    await().until(() -> result.toMaybe().isSome());

    assertThat(visited1.get()).isTrue();
    assertThat(visited2.get()).isTrue();
    assertThat(result.get()).isEqualTo(some(list(tuple(), tuple())));
  }

  private static Initializable initializable(AtomicBoolean visited1) {
    return () -> {
      visited1.set(true);
      return output(tuple(), label(), list());
    };
  }
}
