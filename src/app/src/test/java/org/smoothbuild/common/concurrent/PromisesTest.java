package org.smoothbuild.common.concurrent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.common.concurrent.Promises.runWhenAllAvailable;

import io.vavr.collection.Array;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class PromisesTest {
  @Nested
  class run_when_all_available {
    @Test
    void calls_runnable_when_all_children_become_available() {
      Runnable parent = mock(Runnable.class);
      Array<PromisedValue<String>> promised = Array.fill(3, PromisedValue::new);
      runWhenAllAvailable(promised, parent);
      for (PromisedValue<String> child : promised) {
        child.accept("abc");
      }

      verify(parent, times(1)).run();
    }

    @Test
    void calls_runnable_immediately_when_all_children_were_available_before_call() {
      Runnable parent = mock(Runnable.class);
      Array<PromisedValue<String>> promised = Array.fill(3, PromisedValue::new);
      for (PromisedValue<String> child : promised) {
        child.accept("abc");
      }
      runWhenAllAvailable(promised, parent);

      verify(parent, times(1)).run();
    }

    @Test
    void is_not_run_when_not_all_children_are_available() {
      Runnable parent = mock(Runnable.class);
      Array<PromisedValue<String>> promised = Array.fill(3, PromisedValue::new);
      runWhenAllAvailable(promised, parent);
      for (int i = 1; i < promised.size(); i++) {
        promised.get(i).accept("abc");
      }

      verifyNoInteractions(parent);
    }
  }
}
