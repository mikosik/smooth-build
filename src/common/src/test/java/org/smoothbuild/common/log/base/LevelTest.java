package org.smoothbuild.common.log.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.log.base.Level.ERROR;
import static org.smoothbuild.common.log.base.Level.FATAL;
import static org.smoothbuild.common.log.base.Level.INFO;
import static org.smoothbuild.common.log.base.Level.WARNING;

import org.junit.jupiter.api.Test;

public class LevelTest {
  @Test
  void hasPriorityAtLeast() {
    assertThat(FATAL.hasPriorityAtLeast(FATAL)).isTrue();
    assertThat(FATAL.hasPriorityAtLeast(ERROR)).isTrue();
    assertThat(FATAL.hasPriorityAtLeast(WARNING)).isTrue();
    assertThat(FATAL.hasPriorityAtLeast(INFO)).isTrue();

    assertThat(ERROR.hasPriorityAtLeast(FATAL)).isFalse();
    assertThat(ERROR.hasPriorityAtLeast(ERROR)).isTrue();
    assertThat(ERROR.hasPriorityAtLeast(WARNING)).isTrue();
    assertThat(ERROR.hasPriorityAtLeast(INFO)).isTrue();

    assertThat(WARNING.hasPriorityAtLeast(FATAL)).isFalse();
    assertThat(WARNING.hasPriorityAtLeast(ERROR)).isFalse();
    assertThat(WARNING.hasPriorityAtLeast(WARNING)).isTrue();
    assertThat(WARNING.hasPriorityAtLeast(INFO)).isTrue();

    assertThat(INFO.hasPriorityAtLeast(FATAL)).isFalse();
    assertThat(INFO.hasPriorityAtLeast(ERROR)).isFalse();
    assertThat(INFO.hasPriorityAtLeast(WARNING)).isFalse();
    assertThat(INFO.hasPriorityAtLeast(INFO)).isTrue();
  }
}
