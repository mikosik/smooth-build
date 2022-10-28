package org.smoothbuild.out.log;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.out.log.ImmutableLogs.logs;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.out.log.Log.info;
import static org.smoothbuild.out.log.Log.warning;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class ImmutableLogsTest {
  @Nested
  class _contains_at_least_error_returns {
    @Test
    public void false_when_only_info_log_is_available() {
      ImmutableLogs logs = logs(info("message"));
      assertThat(logs.containsAtLeast(ERROR))
          .isFalse();
    }

    @Test
    public void false_when_only_warning_log_is_available() {
      ImmutableLogs logs = logs(warning("message"));
      assertThat(logs.containsAtLeast(ERROR))
          .isFalse();
    }

    @Test
    public void true_when_only_error_log_is_available() {
      ImmutableLogs logs = logs(error("message"));
      assertThat(logs.containsAtLeast(ERROR))
          .isTrue();
    }

    @Test
    public void true_when_only_fatal_log_is_available() {
      ImmutableLogs logs = logs(fatal("message"));
      assertThat(logs.containsAtLeast(ERROR))
          .isTrue();
    }
  }

  @Test
  public void equals_and_hashcode() {
    new EqualsTester()
        .addEqualityGroup(
            logs(),
            logs())
        .addEqualityGroup(
            logs(error("first")),
            logs(error("first")))
        .addEqualityGroup(
            logs(error("first"), error("second")),
            logs(error("first"), error("second")))
        .addEqualityGroup(
            logs(error("second")),
            logs(error("second")))
        .testEquals();
  }
}
