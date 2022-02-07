package org.smoothbuild.out.log;

import static com.google.common.truth.Truth.assertThat;
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
      ImmutableLogs logs = ImmutableLogs.logs(Log.info("message"));
      assertCall(() -> Maybe.maybeLogs(logs))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void null_value_is_passed_and_logs_have_no_problem() {
      ImmutableLogs logs = ImmutableLogs.logs(Log.info("message"));
      assertCall(() -> Maybe.maybeValueAndLogs(null, logs))
          .throwsException(IllegalArgumentException.class);
    }
  }

  @Nested
  class _value {
    @Test
    public void returns_stored_value() {
      var maybe = Maybe.maybeValue("abc");
      assertThat(maybe.value())
          .isEqualTo("abc");
    }

    @Test
    public void throws_exception_when_no_value_is_stored() {
      LogBuffer logs = new LogBuffer();
      logs.error("message");
      var maybe = Maybe.maybeLogs(logs);
      assertCall(maybe::value)
          .throwsException(IllegalStateException.class);
    }
  }

  @Nested
  class _value_optional {
    @Test
    public void returns_stored_value() {
      var maybe = Maybe.maybeValue("abc");
      assertThat(maybe.valueOptional())
          .isEqualTo(Optional.of("abc"));
    }

    @Test
    public void returns_empty_when_no_value_is_stored() {
      ImmutableLogs logs = ImmutableLogs.logs(Log.error("message"));
      var maybe = Maybe.maybeLogs(logs);
      assertThat(maybe.valueOptional())
          .isEqualTo(Optional.empty());
    }
  }

  @Nested
  class _has_problems {
    @Test
    public void returns_false_when_only_value_is_present() {
      var maybe = Maybe.maybeValue("abc");
      assertThat(maybe.containsProblem())
          .isFalse();
    }

    @Test
    public void returns_false_when_only_info_log_is_present() {
      ImmutableLogs logs = ImmutableLogs.logs(Log.info("message"));
      var maybe = Maybe.maybeValueAndLogs("abc", logs);
      assertThat(maybe.containsProblem())
          .isFalse();
    }

    @Test
    public void returns_false_when_only_warning_log_is_present() {
      ImmutableLogs logs = ImmutableLogs.logs(Log.warning("message"));
      var maybe = Maybe.maybeValueAndLogs("abc", logs);
      assertThat(maybe.containsProblem())
          .isFalse();
    }

    @Test
    public void returns_true_when_error_log_is_present() {
      ImmutableLogs logs = ImmutableLogs.logs(Log.error("message"));
      var maybe = Maybe.maybeValueAndLogs("abc", logs);
      assertThat(maybe.containsProblem())
          .isTrue();
    }

    @Test
    public void returns_true_when_fatal_log_is_present() {
      ImmutableLogs logs = ImmutableLogs.logs(Log.fatal("message"));
      var maybe = Maybe.maybeValueAndLogs("abc", logs);
      assertThat(maybe.containsProblem())
          .isTrue();
    }
  }

  @Test
  public void test_equals_and_hashcode() {
    new EqualsTester()
        .addEqualityGroup(Maybe.maybeValue("abc"), Maybe.maybeValue("abc"))
        .addEqualityGroup(Maybe.maybeValue("def"), Maybe.maybeValue("def"))
        .addEqualityGroup(withLog("abc"), withLog("abc"))
        .addEqualityGroup(withLog("def"), withLog("def"));
  }

  @Test
  public void to_string() {
    ImmutableLogs logs = ImmutableLogs.logs(Log.error("message"));
    Maybe<String> maybe = Maybe.maybeValueAndLogs("abc", logs);
    assertThat(maybe.toString())
        .isEqualTo("Maybe{abc, [Log{ERROR, 'message'}]}");
  }

  private static Maybe<String> withLog(String message) {
    return Maybe.maybeLogs(ImmutableLogs.logs(Log.error(message)));
  }
}
