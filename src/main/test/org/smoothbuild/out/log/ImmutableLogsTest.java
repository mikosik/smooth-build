package org.smoothbuild.out.log;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.out.log.ImmutableLogs.logs;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.out.log.Log.info;
import static org.smoothbuild.out.log.Log.warning;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ImmutableLogsTest {
  @Nested
  class _contains_problem_returns {
    @Test
    public void false_when_only_info_log_is_available() {
      ImmutableLogs logs = logs(info("message"));
      assertThat(logs.containsProblem())
          .isFalse();
    }

    @Test
    public void false_when_only_warning_log_is_available() {
      ImmutableLogs logs = logs(warning("message"));
      assertThat(logs.containsProblem())
          .isFalse();
    }

    @Test
    public void true_when_only_error_log_is_available() {
      ImmutableLogs logs = logs(error("message"));
      assertThat(logs.containsProblem())
          .isTrue();
    }

    @Test
    public void true_when_only_fatal_log_is_available() {
      ImmutableLogs logs = logs(fatal("message"));
      assertThat(logs.containsProblem())
          .isTrue();
    }
  }
}
