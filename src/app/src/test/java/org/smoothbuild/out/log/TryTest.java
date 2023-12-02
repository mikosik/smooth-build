package org.smoothbuild.out.log;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.option.Maybe.none;
import static org.smoothbuild.common.option.Maybe.some;
import static org.smoothbuild.out.log.ImmutableLogs.logs;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.out.log.Log.info;
import static org.smoothbuild.out.log.Log.warning;
import static org.smoothbuild.out.log.TestingLog.ERROR_LOG;
import static org.smoothbuild.out.log.TestingLog.INFO_LOG;
import static org.smoothbuild.out.log.TestingLog.WARNING_LOG;
import static org.smoothbuild.out.log.Try.failure;
import static org.smoothbuild.out.log.Try.success;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TryTest {
  @Nested
  class _try_of {
    @Test
    void creation_with_value_and_non_problem() {
      var tryOf = Try.of("abc", WARNING_LOG);
      assertThat(tryOf.value()).isEqualTo("abc");
      assertThat(tryOf.logs()).isEqualTo(logs(WARNING_LOG));
    }

    @Test
    void creation_with_value_and_problem() {
      var tryOf = Try.of("abc", ERROR_LOG);
      assertThat(tryOf.toMaybe()).isEqualTo(none());
      assertThat(tryOf.logs()).isEqualTo(logs(ERROR_LOG));
    }
  }

  @Nested
  class _success {
    @Test
    public void has_value() {
      var success = success("abc");
      assertThat(success.value()).isEqualTo("abc");
    }

    @Test
    public void toMaybe_returns_some_with_value() {
      var success = success("abc");
      assertThat(success.toMaybe()).isEqualTo(some("abc"));
    }

    @Test
    public void creation_with_non_problem_log_is_allowed() {
      var success = success("abc", WARNING_LOG);
      assertThat(success.value()).isEqualTo("abc");
    }

    @Test
    public void creation_with_problem_fails() {
      assertCall(() -> success("abc", ERROR_LOG)).throwsException(IllegalArgumentException.class);
    }

    @Test
    public void creation_with_null_value_fails() {
      assertCall(() -> success(null)).throwsException(IllegalArgumentException.class);
    }
  }

  @Nested
  class _failure {
    @Test
    public void creation_with_no_failure_fails() {
      ImmutableLogs logs = logs(INFO_LOG);
      assertCall(() -> failure(logs)).throwsException(IllegalArgumentException.class);
    }

    @Test
    public void has_no_value() {
      var failure = failure(ERROR_LOG);
      assertCall(failure::value).throwsException(IllegalStateException.class);
    }

    @Test
    public void toMaybe_returns_none() {
      var failure = failure(ERROR_LOG);
      assertThat(failure.toMaybe()).isEqualTo(none());
    }
  }

  @Test
  public void test_equals_and_hashcode() {
    new EqualsTester()
        .addEqualityGroup(success("abc"), success("abc"))
        .addEqualityGroup(success("def"), success("def"))
        .addEqualityGroup(failure(fatal("abc")), failure(fatal("abc")), failure(logs(fatal("abc"))))
        .addEqualityGroup(failure(error("abc")), failure(error("abc")), failure(logs(error("abc"))))
        .addEqualityGroup(failure(error("def")), failure(error("def")), failure(logs(error("def"))))
        .addEqualityGroup(
            success("abc", warning("abc")),
            success("abc", warning("abc")),
            success("abc", logs(warning("abc"))))
        .addEqualityGroup(
            success("abc", info("abc")),
            success("abc", info("abc")),
            success("abc", logs(info("abc"))));
  }

  @Test
  public void to_string() {
    var success = success("abc", info("message"));
    assertThat(success.toString()).isEqualTo("Try{abc, [Log{INFO, 'message'}]}");
  }
}
