package org.smoothbuild.util.concurrent;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;

public class ThresholdRunnableTest {
  @Test
  public void negative_count_causes_exception() {
    IllegalArgumentException e =
        assertThrows(IllegalArgumentException.class, () -> new ThresholdRunnable(-1, () -> {}));
    assertThat(e.getMessage())
        .isEqualTo("'count' argument is -1 but should be 0 or more.");
  }

  @Test
  public void null_runnable_causes_exception() {
    assertThrows(NullPointerException.class, () -> new ThresholdRunnable(1, null));
  }

  @Test
  public void runnable_is_called_from_constructor_when_threshold_is_0() {
    Runnable runnable = mock(Runnable.class);
    new ThresholdRunnable(0, runnable);

    verify(runnable, only()).run();
  }

  @Test
  public void runnable_is_not_called_when_threshold_is_not_reached() {
    Runnable runnable = mock(Runnable.class);
    ThresholdRunnable thresholdRunnable = new ThresholdRunnable(3, runnable);
    invokeNTimes(2, thresholdRunnable);

    verifyNoInteractions(runnable);
  }

  @Test
  public void runnable_is_called_when_threshold_is_reached() {
    Runnable runnable = mock(Runnable.class);
    ThresholdRunnable thresholdRunnable = new ThresholdRunnable(3, runnable);
    invokeNTimes(3, thresholdRunnable);

    verify(runnable, only()).run();
  }

  @Test
  public void runnable_is_called_only_once_when_threshold_is_reached_exceeded() {
    Runnable runnable = mock(Runnable.class);
    ThresholdRunnable thresholdRunnable = new ThresholdRunnable(3, runnable);
    invokeNTimes(4, thresholdRunnable);

    verify(runnable, only()).run();
  }

  private static void invokeNTimes(int count, ThresholdRunnable thresholdRunnable) {
    for (int i = 0; i < count; i++) {
      thresholdRunnable.run();
    }
  }
}
