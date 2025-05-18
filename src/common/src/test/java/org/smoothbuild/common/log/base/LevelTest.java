package org.smoothbuild.common.log.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.log.base.Level.ERROR;
import static org.smoothbuild.common.log.base.Level.FATAL;
import static org.smoothbuild.common.log.base.Level.INFO;
import static org.smoothbuild.common.log.base.Level.WARNING;

import org.junit.jupiter.api.Test;

public class LevelTest {
  @Test
  void hasSeverityAtLeast() {
    assertThat(FATAL.hasSeverityAtLeast(FATAL)).isTrue();
    assertThat(FATAL.hasSeverityAtLeast(ERROR)).isTrue();
    assertThat(FATAL.hasSeverityAtLeast(WARNING)).isTrue();
    assertThat(FATAL.hasSeverityAtLeast(INFO)).isTrue();

    assertThat(ERROR.hasSeverityAtLeast(FATAL)).isFalse();
    assertThat(ERROR.hasSeverityAtLeast(ERROR)).isTrue();
    assertThat(ERROR.hasSeverityAtLeast(WARNING)).isTrue();
    assertThat(ERROR.hasSeverityAtLeast(INFO)).isTrue();

    assertThat(WARNING.hasSeverityAtLeast(FATAL)).isFalse();
    assertThat(WARNING.hasSeverityAtLeast(ERROR)).isFalse();
    assertThat(WARNING.hasSeverityAtLeast(WARNING)).isTrue();
    assertThat(WARNING.hasSeverityAtLeast(INFO)).isTrue();

    assertThat(INFO.hasSeverityAtLeast(FATAL)).isFalse();
    assertThat(INFO.hasSeverityAtLeast(ERROR)).isFalse();
    assertThat(INFO.hasSeverityAtLeast(WARNING)).isFalse();
    assertThat(INFO.hasSeverityAtLeast(INFO)).isTrue();
  }
}
