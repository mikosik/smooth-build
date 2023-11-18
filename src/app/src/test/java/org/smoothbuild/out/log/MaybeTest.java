package org.smoothbuild.out.log;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.out.log.ImmutableLogs.logs;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.out.log.Log.info;
import static org.smoothbuild.out.log.Log.warning;
import static org.smoothbuild.out.log.Maybe.failure;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.out.log.Maybe.success;
import static org.smoothbuild.out.log.TestingLog.ERROR_LOG;
import static org.smoothbuild.out.log.TestingLog.INFO_LOG;
import static org.smoothbuild.out.log.TestingLog.WARNING_LOG;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class MaybeTest {
  @Nested
  class _maybe {
    @Test
    void creation_with_value_and_non_problem() {
      var maybe = maybe("abc", WARNING_LOG);
      assertThat(maybe.value())
          .isEqualTo("abc");
      assertThat(maybe.logs())
          .isEqualTo(logs(WARNING_LOG));
    }

    @Test
    void creation_with_value_and_problem() {
      var maybe = maybe("abc", ERROR_LOG);
      assertThat(maybe.valueOptional())
          .isEqualTo(Optional.empty());
      assertThat(maybe.logs())
          .isEqualTo(logs(ERROR_LOG));
    }
  }

  @Nested
  class _success {
    @Test
    public void has_value() {
      var maybe = success("abc");
      assertThat(maybe.value())
          .isEqualTo("abc");
    }

    @Test
    public void has_value_optional() {
      var maybe = success("abc");
      assertThat(maybe.valueOptional())
          .isEqualTo(Optional.of("abc"));
    }

    @Test
    public void creation_with_non_problem_log_is_allowed() {
      var maybe = success("abc", WARNING_LOG);
      assertThat(maybe.value())
          .isEqualTo("abc");
    }

    @Test
    public void creation_with_problem_fails() {
      assertCall(() -> success("abc", ERROR_LOG))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void creation_with_null_value_fails() {
      assertCall(() -> success(null))
          .throwsException(IllegalArgumentException.class);
    }
  }

  @Nested
  class _failure {
    @Test
    public void creation_with_no_failure_fails() {
      ImmutableLogs logs = logs(INFO_LOG);
      assertCall(() -> failure(logs))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void has_no_value() {
      var maybe = failure(ERROR_LOG);
      assertCall(maybe::value)
          .throwsException(IllegalStateException.class);
    }

    @Test
    public void has_no_value_optional() {
      var maybe = failure(ERROR_LOG);
      assertThat(maybe.valueOptional())
          .isEqualTo(Optional.empty());
    }
  }

  @Test
  public void test_equals_and_hashcode() {
    new EqualsTester()
        .addEqualityGroup(
            success("abc"),
            success("abc"))
        .addEqualityGroup(
            success("def"),
            success("def"))
        .addEqualityGroup(
            failure(fatal("abc")),
            failure(fatal("abc")),
            failure(logs(fatal("abc"))))
        .addEqualityGroup(
            failure(error("abc")),
            failure(error("abc")),
            failure(logs(error("abc"))))
        .addEqualityGroup(
            failure(error("def")),
            failure(error("def")),
            failure(logs(error("def"))))
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
    var maybe = success("abc", info("message"));
    assertThat(maybe.toString())
        .isEqualTo("Maybe{abc, [Log{INFO, 'message'}]}");
  }
}
