package org.smoothbuild.out.log;

import static com.google.common.truth.Truth.assertThat;

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
  class _containsProblem {
    @Test
    public void when_nothing_has_been_logged_returns_false() {
      assertThat(value.containsProblem())
          .isFalse();
    }

    @Test
    public void after_logging_fatal_returns_true() {
      value.log(Log.fatal("message"));
      assertThat(value.containsProblem())
          .isTrue();
    }

    @Test
    public void after_warning_error_returns_true() {
      value.log(Log.error("message"));
      assertThat(value.containsProblem())
          .isTrue();
    }

    @Test
    public void after_logging_warning_returns_false() {
      value.log(Log.warning("message"));
      assertThat(value.containsProblem())
          .isFalse();
    }

    @Test
    public void after_logging_info_returns_false() {
      value.log(Log.info("message"));
      assertThat(value.containsProblem())
          .isFalse();
    }

    @Test
    public void after_adding_logs_from_other_logger_with_logs_containing_problems_returns_true() {
      value.logAll(loggerWith(Log.error("message")));
      assertThat(value.containsProblem())
          .isTrue();
    }

    @Test
    public void after_logging_fatal_and_adding_logs_from_other_logger_without_problems_returns_true() {
      value.log(Log.fatal("message"));
      value.logAll(loggerWith(Log.info("message")));
      assertThat(value.containsProblem())
          .isTrue();
    }

    @Test
    public void after_logging_error_and_adding_logs_from_other_logger_without_problems_returns_true() {
      value.log(Log.error("message"));
      value.logAll(loggerWith(Log.info("message")));
      assertThat(value.containsProblem())
          .isTrue();
    }

    @Test
    public void after_logging_warning_and_adding_logs_from_other_logger_with_error_returns_true() {
      value.log(Log.warning("message"));
      value.logAll(loggerWith(Log.error("message")));
      assertThat(value.containsProblem())
          .isTrue();
    }
  }

  @Test
  public void logs_contains_all_logs() {
    Log fatal = Log.fatal("fatal");
    Log error = Log.error("error");
    Log warning = Log.warning("warning");
    Log info = Log.info("info");
    Log other = Log.info("info");

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
