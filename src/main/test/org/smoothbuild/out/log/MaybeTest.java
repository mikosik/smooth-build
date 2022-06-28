package org.smoothbuild.out.log;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.out.log.ImmutableLogs.logs;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.out.log.Log.info;
import static org.smoothbuild.out.log.Log.warning;
import static org.smoothbuild.out.log.Maybe.maybeLogs;
import static org.smoothbuild.out.log.Maybe.maybeValue;
import static org.smoothbuild.out.log.Maybe.maybeValueAndLogs;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class MaybeTest {
  @Nested
  class _creating_fails_when {
    @Test
    public void no_value_is_passed_and_logs_have_no_problem() {
      ImmutableLogs logs = logs(info("message"));
      assertCall(() -> maybeLogs(logs))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void null_value_is_passed_and_logs_have_no_problem() {
      assertCall(() -> maybeValueAndLogs(null, info("message")))
          .throwsException(IllegalArgumentException.class);
    }
  }

  @Nested
  class _value {
    @Test
    public void returns_stored_value() {
      var maybe = maybeValue("abc");
      assertThat(maybe.value())
          .isEqualTo("abc");
    }

    @Test
    public void throws_exception_when_no_value_is_stored() {
      var maybe = maybeLogs(error("message"));
      assertCall(maybe::value)
          .throwsException(IllegalStateException.class);
    }
  }

  @Nested
  class _value_optional {
    @Test
    public void returns_stored_value() {
      var maybe = maybeValue("abc");
      assertThat(maybe.valueOptional())
          .isEqualTo(Optional.of("abc"));
    }

    @Test
    public void returns_empty_when_no_value_is_stored() {
      var maybe = maybeLogs(error("message"));
      assertThat(maybe.valueOptional())
          .isEqualTo(Optional.empty());
    }
  }

  @Nested
  class _has_problems {
    @Test
    public void returns_false_when_only_value_is_present() {
      var maybe = maybeValue("abc");
      assertThat(maybe.containsProblem())
          .isFalse();
    }

    @Test
    public void returns_false_when_only_info_log_is_present() {
      var maybe = maybeValueAndLogs("abc", info("message"));
      assertThat(maybe.containsProblem())
          .isFalse();
    }

    @Test
    public void returns_false_when_only_warning_log_is_present() {
      var maybe = maybeValueAndLogs("abc", warning("message"));
      assertThat(maybe.containsProblem())
          .isFalse();
    }

    @Test
    public void returns_true_when_error_log_is_present() {
      var maybe = maybeValueAndLogs(null, error("message"));
      assertThat(maybe.containsProblem())
          .isTrue();
    }

    @Test
    public void returns_true_when_fatal_log_is_present() {
      var maybe = maybeValueAndLogs(null, fatal("message"));
      assertThat(maybe.containsProblem())
          .isTrue();
    }
  }

  @Test
  public void test_equals_and_hashcode() {
    new EqualsTester()
        .addEqualityGroup(
            maybeValue("abc"),
            maybeValue("abc"))
        .addEqualityGroup(
            maybeValue("def"),
            maybeValue("def"))
        .addEqualityGroup(
            maybeLogs(fatal("abc")),
            maybeLogs(fatal("abc")),
            maybeLogs(logs(fatal("abc"))))
        .addEqualityGroup(
            maybeLogs(error("abc")),
            maybeLogs(error("abc")),
            maybeLogs(logs(error("abc"))))
        .addEqualityGroup(
            maybeLogs(error("def")),
            maybeLogs(error("def")),
            maybeLogs(logs(error("def"))))
        .addEqualityGroup(
            maybeValueAndLogs("abc", warning("abc")),
            maybeValueAndLogs("abc", warning("abc")),
            maybeValueAndLogs("abc", logs(warning("abc"))))
        .addEqualityGroup(
            maybeValueAndLogs("abc", info("abc")),
            maybeValueAndLogs("abc", info("abc")),
            maybeValueAndLogs("abc", logs(info("abc"))));
  }

  @Test
  public void to_string() {
    Maybe<String> maybe = maybeValueAndLogs("abc", info("message"));
    assertThat(maybe.toString())
        .isEqualTo("Maybe{abc, [Log{INFO, 'message'}]}");
  }
}
