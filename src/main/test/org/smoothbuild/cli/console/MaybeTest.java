package org.smoothbuild.cli.console;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.cli.console.Log.fatal;
import static org.smoothbuild.cli.console.Log.info;
import static org.smoothbuild.cli.console.Log.warning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MaybeTest {
  private Maybe<String> value;

  @BeforeEach
  public void before() {
    value = new Maybe<>();
  }

  @Nested
  class value {
    @Test
    public void is_initially_null() {
      assertThat(value.value())
          .isNull();
    }

    @Test
    public void returns_previously_set_value() {
      value.setValue("abc");
      assertThat(value.value())
          .isEqualTo("abc");
    }
  }

  @Nested
  class hasProblems {
    @Test
    public void when_nothing_has_been_logged_returns_false() {
      assertThat(value.hasProblems())
          .isFalse();
    }

    @Test
    public void after_logging_fatal_returns_true() {
      value.log(fatal("message"));
      assertThat(value.hasProblems())
          .isTrue();
    }

    @Test
    public void after_warning_error_returns_true() {
      value.log(error("message"));
      assertThat(value.hasProblems())
          .isTrue();
    }

    @Test
    public void after_logging_warning_returns_false() {
      value.log(warning("message"));
      assertThat(value.hasProblems())
          .isFalse();
    }

    @Test
    public void after_logging_info_returns_false() {
      value.log(info("message"));
      assertThat(value.hasProblems())
          .isFalse();
    }

    @Test
    public void after_adding_other_value_with_logs_containing_problems_returns_true() {
      value.addLogs(valueWith(error("message")));
      assertThat(value.hasProblems())
          .isTrue();
    }


    @Test
    public void after_logging_fatal_and_adding_other_value_without_problems_returns_true() {
      value.log(fatal("message"));
      value.addLogs(valueWith(info("message")));
      assertThat(value.hasProblems())
          .isTrue();
    }

    @Test
    public void after_logging_error_and_adding_other_value_without_problems_returns_true() {
      value.log(error("message"));
      value.addLogs(valueWith(info("message")));
      assertThat(value.hasProblems())
          .isTrue();
    }

    @Test
    public void after_logging_warning_and_adding_value_with_error_returns_true() {
      value.log(warning("message"));
      value.addLogs(valueWith(error("message")));
      assertThat(value.hasProblems())
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
    value.addLogs(valueWith(other));

    assertThat(value.logs())
        .containsExactly(fatal, error, warning, info, other)
        .inOrder();
  }

  private static Maybe<Object> valueWith(Log log) {
    Maybe<Object> otherValue = new Maybe<>();
    otherValue.log(log);
    return otherValue;
  }
}
