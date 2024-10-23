package org.smoothbuild.common.concurrent;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.concurrent.Promise.runWhenAllAvailable;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;

public class PromiseTest {
  @Nested
  class _equality {
    @Test
    void equals_and_hash_code() {
      new EqualsTester()
          .addEqualityGroup(
              promise("abc"), promise("abc"), mutablePromise("abc"), mutablePromise("abc"))
          .addEqualityGroup(
              promise("def"), promise("def"), mutablePromise("def"), mutablePromise("def"))
          .addEqualityGroup(promise(), promise())
          .testEquals();
    }

    private static <T> MutablePromise<T> mutablePromise(T value) {
      MutablePromise<T> promise = promise();
      promise.accept(value);
      return promise;
    }
  }

  @Nested
  class map {
    @Test
    void promise_created_by_map_is_empty_if_original_is_empty() {
      MutablePromise<String> promise = promise();
      var mapped = promise.map(s -> s + "!");
      assertThat(mapped.toMaybe().isNone()).isTrue();
    }

    @Test
    void promise_created_by_map_contains_mapped_value() {
      MutablePromise<String> promise = promise();
      var mapped = promise.map(s -> s + "!");
      promise.accept("abc");
      assertThat(mapped.get()).isEqualTo("abc!");
    }
  }

  @Nested
  class run_when_all_available {
    @Test
    void calls_runnable_when_all_children_become_available() {
      Runnable parent = mock(Runnable.class);
      List<MutablePromise<String>> promised =
          list(new MutablePromise<>(), new MutablePromise<>(), new MutablePromise<>());
      runWhenAllAvailable(promised, parent);
      for (MutablePromise<String> child : promised) {
        child.accept("abc");
      }

      verify(parent, times(1)).run();
    }

    @Test
    void calls_runnable_immediately_when_all_children_were_available_before_call() {
      Runnable parent = mock(Runnable.class);
      List<MutablePromise<String>> promised =
          list(new MutablePromise<>(), new MutablePromise<>(), new MutablePromise<>());
      for (MutablePromise<String> child : promised) {
        child.accept("abc");
      }
      runWhenAllAvailable(promised, parent);

      verify(parent, times(1)).run();
    }

    @Test
    void is_not_run_when_not_all_children_are_available() {
      Runnable parent = mock(Runnable.class);
      List<MutablePromise<String>> promised =
          list(new MutablePromise<>(), new MutablePromise<>(), new MutablePromise<>());
      runWhenAllAvailable(promised, parent);
      for (int i = 1; i < promised.size(); i++) {
        promised.get(i).accept("abc");
      }

      verifyNoInteractions(parent);
    }
  }
}
