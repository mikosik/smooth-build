package org.smoothbuild.out.log;

import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

public class LevelTest {
  @Test
  void hasPriorityAtLeast() {
    Truth.assertThat(Level.FATAL.hasPriorityAtLeast(Level.FATAL)).isTrue();
    Truth.assertThat(Level.FATAL.hasPriorityAtLeast(Level.ERROR)).isTrue();
    Truth.assertThat(Level.FATAL.hasPriorityAtLeast(Level.WARNING)).isTrue();
    Truth.assertThat(Level.FATAL.hasPriorityAtLeast(Level.INFO)).isTrue();

    Truth.assertThat(Level.ERROR.hasPriorityAtLeast(Level.FATAL)).isFalse();
    Truth.assertThat(Level.ERROR.hasPriorityAtLeast(Level.ERROR)).isTrue();
    Truth.assertThat(Level.ERROR.hasPriorityAtLeast(Level.WARNING)).isTrue();
    Truth.assertThat(Level.ERROR.hasPriorityAtLeast(Level.INFO)).isTrue();

    Truth.assertThat(Level.WARNING.hasPriorityAtLeast(Level.FATAL)).isFalse();
    Truth.assertThat(Level.WARNING.hasPriorityAtLeast(Level.ERROR)).isFalse();
    Truth.assertThat(Level.WARNING.hasPriorityAtLeast(Level.WARNING)).isTrue();
    Truth.assertThat(Level.WARNING.hasPriorityAtLeast(Level.INFO)).isTrue();

    Truth.assertThat(Level.INFO.hasPriorityAtLeast(Level.FATAL)).isFalse();
    Truth.assertThat(Level.INFO.hasPriorityAtLeast(Level.ERROR)).isFalse();
    Truth.assertThat(Level.INFO.hasPriorityAtLeast(Level.WARNING)).isFalse();
    Truth.assertThat(Level.INFO.hasPriorityAtLeast(Level.INFO)).isTrue();
  }
}
