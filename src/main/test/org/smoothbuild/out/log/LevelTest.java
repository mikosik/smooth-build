package org.smoothbuild.out.log;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

public class LevelTest {
  @Test
  void hasPriorityAtLeast() {
    assertThat(Level.FATAL.hasPriorityAtLeast(Level.FATAL)).isTrue();
    assertThat(Level.FATAL.hasPriorityAtLeast(Level.ERROR)).isTrue();
    assertThat(Level.FATAL.hasPriorityAtLeast(Level.WARNING)).isTrue();
    assertThat(Level.FATAL.hasPriorityAtLeast(Level.INFO)).isTrue();

    assertThat(Level.ERROR.hasPriorityAtLeast(Level.FATAL)).isFalse();
    assertThat(Level.ERROR.hasPriorityAtLeast(Level.ERROR)).isTrue();
    assertThat(Level.ERROR.hasPriorityAtLeast(Level.WARNING)).isTrue();
    assertThat(Level.ERROR.hasPriorityAtLeast(Level.INFO)).isTrue();

    assertThat(Level.WARNING.hasPriorityAtLeast(Level.FATAL)).isFalse();
    assertThat(Level.WARNING.hasPriorityAtLeast(Level.ERROR)).isFalse();
    assertThat(Level.WARNING.hasPriorityAtLeast(Level.WARNING)).isTrue();
    assertThat(Level.WARNING.hasPriorityAtLeast(Level.INFO)).isTrue();

    assertThat(Level.INFO.hasPriorityAtLeast(Level.FATAL)).isFalse();
    assertThat(Level.INFO.hasPriorityAtLeast(Level.ERROR)).isFalse();
    assertThat(Level.INFO.hasPriorityAtLeast(Level.WARNING)).isFalse();
    assertThat(Level.INFO.hasPriorityAtLeast(Level.INFO)).isTrue();
  }
}
