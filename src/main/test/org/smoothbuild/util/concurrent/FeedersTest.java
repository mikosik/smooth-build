package org.smoothbuild.util.concurrent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.util.concurrent.Feeders.runWhenAllAvailable;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("ClassCanBeStatic")
public class FeedersTest {
  @Nested
  class run_when_all_available {
    @Test
    void calls_runnable_when_all_children_become_available() {
      Runnable parent = mock(Runnable.class);
      List<FeedingConsumer<String>>
          feeders = List.of(new FeedingConsumer<>(), new FeedingConsumer<>(), new FeedingConsumer<>());
      runWhenAllAvailable(feeders, parent);
      for (FeedingConsumer<String> child : feeders) {
        child.accept("abc");
      }

      verify(parent, times(1)).run();
    }

    @Test
    void calls_runnable_immediately_when_all_children_were_available_before_call() {
      Runnable parent = mock(Runnable.class);
      List<FeedingConsumer<String>> feeders = List.of(new FeedingConsumer<>(), new FeedingConsumer<>(), new FeedingConsumer<>());
      for (FeedingConsumer<String> child : feeders) {
        child.accept("abc");
      }
      runWhenAllAvailable(feeders, parent);

      verify(parent, times(1)).run();
    }

    @Test
    void is_not_run_when_not_all_children_are_available() {
      Runnable parent = mock(Runnable.class);
      List<FeedingConsumer<String>> feeders = List.of(new FeedingConsumer<>(), new FeedingConsumer<>(), new FeedingConsumer<>());
      runWhenAllAvailable(feeders, parent);
      for (int i = 1; i < feeders.size(); i++) {
        feeders.get(i).accept("abc");
      }

      verifyNoInteractions(parent);
    }
  }
}
