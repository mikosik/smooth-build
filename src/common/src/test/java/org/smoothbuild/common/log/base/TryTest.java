package org.smoothbuild.common.log.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.log.base.Log.warning;
import static org.smoothbuild.common.log.base.Try.failure;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TryTest {
  @Nested
  class _try_of {
    @Nested
    class _with_null_value {
      @Test
      void and_no_failure() {
        var tryOf = Try.of(null, warning("warning message"));
        assertThat(tryOf.value()).isNull();
        assertThat(tryOf.logs()).isEqualTo(list(warning("warning message")));
      }

      @Test
      void and_failure() {
        var tryOf = Try.of(null, error("error message"));
        assertThat(tryOf.toMaybe()).isEqualTo(none());
        assertThat(tryOf.logs()).isEqualTo(list(error("error message")));
      }
    }

    @Nested
    class _with_non_null_value {
      @Test
      void and_no_failure() {
        var tryOf = Try.of("abc", warning("warning message"));
        assertThat(tryOf.value()).isEqualTo("abc");
        assertThat(tryOf.logs()).isEqualTo(list(warning("warning message")));
      }

      @Test
      void and_failure() {
        var tryOf = Try.of("abc", error("error message"));
        assertThat(tryOf.toMaybe()).isEqualTo(none());
        assertThat(tryOf.logs()).isEqualTo(list(error("error message")));
      }
    }
  }

  @Nested
  class _success {
    @Nested
    class _with_null_value {
      @Test
      public void has_value() {
        var success = success(null);
        assertThat(success.value()).isNull();
      }

      @Test
      public void toMaybe_returns_some_with_null() {
        var success = success(null);
        assertThat(success.toMaybe()).isEqualTo(some(null));
      }

      @Test
      public void creation_with_non_problem_log_is_allowed() {
        var success = success(null, warning("warning message"));
        assertThat(success.value()).isNull();
      }

      @Test
      public void creation_with_problem_fails() {
        assertCall(() -> success(null, error("error message")))
            .throwsException(IllegalArgumentException.class);
      }
    }

    @Nested
    class _with_non_null_value {
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
        var success = success("abc", warning("warning message"));
        assertThat(success.value()).isEqualTo("abc");
      }

      @Test
      public void creation_with_problem_fails() {
        assertCall(() -> success("abc", error("error message")))
            .throwsException(IllegalArgumentException.class);
      }
    }
  }

  @Nested
  class _failure {
    @Test
    public void creation_with_no_failure_fails() {
      var logs = list(info("info message"));
      assertCall(() -> failure(logs)).throwsException(IllegalArgumentException.class);
    }

    @Test
    public void has_no_value() {
      var failure = failure(error("error message"));
      assertCall(failure::value).throwsException(IllegalStateException.class);
    }

    @Test
    public void toMaybe_returns_none() {
      var failure = failure(error("error message"));
      assertThat(failure.toMaybe()).isEqualTo(none());
    }
  }

  @Test
  public void test_equals_and_hashcode() {
    new EqualsTester()
        .addEqualityGroup(success("abc"), success("abc"))
        .addEqualityGroup(success("def"), success("def"))
        .addEqualityGroup(failure(fatal("abc")), failure(fatal("abc")), failure(list(fatal("abc"))))
        .addEqualityGroup(failure(error("abc")), failure(error("abc")), failure(list(error("abc"))))
        .addEqualityGroup(failure(error("def")), failure(error("def")), failure(list(error("def"))))
        .addEqualityGroup(
            success("abc", warning("abc")),
            success("abc", warning("abc")),
            success("abc", list(warning("abc"))))
        .addEqualityGroup(
            success("abc", info("abc")),
            success("abc", info("abc")),
            success("abc", list(info("abc"))));
  }

  @Test
  public void to_string() {
    var success = success("abc", info("message"));
    assertThat(success.toString()).isEqualTo("Try{Some(abc), [Log{INFO, 'message'}]}");
  }
}
