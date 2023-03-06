package org.smoothbuild.out.log;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.out.log.Log.info;
import static org.smoothbuild.out.log.Log.warning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LogBufferTest {
  private LogBuffer value;

  @BeforeEach
  public void before() {
    value = new LogBuffer();
  }

  @Nested
  class _contains_at_least {
    @Test
    public void when_nothing_has_been_logged_returns_false() {
      assertThat(value.containsAtLeast(ERROR))
          .isFalse();
    }

    @Test
    public void after_logging_fatal_returns_true() {
      value.log(fatal("message"));
      assertThat(value.containsAtLeast(ERROR))
          .isTrue();
    }

    @Test
    public void after_logging_error_returns_true() {
      value.log(error("message"));
      assertThat(value.containsAtLeast(ERROR))
          .isTrue();
    }

    @Test
    public void after_logging_warning_returns_false() {
      value.log(warning("message"));
      assertThat(value.containsAtLeast(ERROR))
          .isFalse();
    }

    @Test
    public void after_logging_info_returns_false() {
      value.log(info("message"));
      assertThat(value.containsAtLeast(ERROR))
          .isFalse();
    }

    @Test
    public void after_adding_logs_from_other_logger_with_logs_contains_at_least_error_returns_true() {
      value.logAll(loggerWith(error("message")));
      assertThat(value.containsAtLeast(ERROR))
          .isTrue();
    }

    @Test
    public void after_logging_fatal_and_adding_logs_from_other_logger_contains_at_least_error_returns_true() {
      value.log(fatal("message"));
      value.logAll(loggerWith(info("message")));
      assertThat(value.containsAtLeast(ERROR))
          .isTrue();
    }

    @Test
    public void after_logging_error_and_adding_logs_from_other_logger_without_problems_returns_true() {
      value.log(error("message"));
      value.logAll(loggerWith(info("message")));
      assertThat(value.containsAtLeast(ERROR))
          .isTrue();
    }

    @Test
    public void after_logging_warning_and_adding_logs_from_other_logger_with_error_returns_true() {
      value.log(warning("message"));
      value.logAll(loggerWith(error("message")));
      assertThat(value.containsAtLeast(ERROR))
          .isTrue();
    }
  }

  @Test
  public void logs_contains_all_logs() {
    Log fatal = fatal("fatal");
    Log error = error("error");
    Log warning = warning("warning");
    Log info = info("info");
    Log other = info("info");

    value.log(fatal);
    value.log(error);
    value.log(warning);
    value.log(info);
    value.logAll(loggerWith(other));

    assertThat(value.toList())
        .containsExactly(fatal, error, warning, info, other)
        .inOrder();
  }

  private static LogBuffer loggerWith(Log log) {
    LogBuffer otherValue = new LogBuffer();
    otherValue.log(log);
    return otherValue;
  }
}
