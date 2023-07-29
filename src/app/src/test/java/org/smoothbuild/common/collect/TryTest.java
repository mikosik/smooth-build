package org.smoothbuild.common.collect;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TryTest {

  @Nested
  class _factory_method {
    @Test
    public void of_throws_exception_when_value_is_null() {
      assertCall(() -> Try.result(null))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void error_throws_exception_when_error_is_null() {
      assertCall(() -> Try.error(null))
          .throwsException(IllegalArgumentException.class);
    }
  }

  @Nested
  class _value {
    @Test
    public void return_value() {
      var result = Try.result(7);
      assertThat(result.result())
          .isEqualTo(7);
    }

    @Test
    public void throws_exception_when_value_is_not_present() {
      var error = Try.error("error");
      assertCall(error::result)
          .throwsException(IllegalStateException.class);
    }
  }

  @Nested
  class _error {
    @Test
    public void return_error() {
      var error = Try.error("error");
      assertThat(error.error())
          .isEqualTo("error");
    }

    @Test
    public void throws_exception_when_value_is_present() {
      var result = Try.result(7);
      assertCall(result::error)
          .throwsException(IllegalStateException.class);
    }
  }

  @Nested
  class _is_present {
    @Test
    public void return_true_when_value_is_present() {
      var result = Try.result(7);
      assertThat(result.isPresent())
          .isTrue();
    }

    @Test
    public void return_false_when_error_is_present() {
      var error = Try.error("error");
      assertThat(error.isPresent())
          .isFalse();
    }
  }

  @Nested
  class _orElse {
    @Test
    public void when_value_is_present() {
      var result = Try.result(7);
      assertThat(result.orElse(e -> 33))
          .isEqualTo(7);
    }

    @Test
    public void when_value_is_not_present() {
      var error = Try.error("some error");
      assertThat(error.orElse(e -> e + "!"))
          .isEqualTo("some error!");
    }
  }

  @Nested
  class _map {
    @Test
    public void mapping_value() {
      var result = Try.result(7);
      assertThat(result.map(Object::toString))
          .isEqualTo(Try.result("7"));
    }

    @Test
    public void mapping_error() {
      var error = Try.error("error");
      assertThat(error.map(Object::toString))
          .isSameInstanceAs(error);
    }
  }

  @Nested
  class _flatMap {
    @Test
    public void mapping_value_to_value() {
      var result = Try.result(7);
      assertThat(result.flatMap(v -> Try.result(v.toString())))
          .isEqualTo(Try.result("7"));
    }

    @Test
    public void mapping_value_to_error() {
      var result = Try.result(7);
      assertThat(result.flatMap(v -> Try.error("error")))
          .isEqualTo(Try.error("error"));
    }

    @Test
    public void mapping_error() {
      var error = Try.error("error");
      assertThat(error.flatMap(v -> Try.result(v.toString())))
          .isSameInstanceAs(error);
    }
  }

  @Nested
  class _mapError {
    @Test
    public void when_value_is_present() {
      var result = Try.result(7);
      assertThat(result.mapError(m -> "error"))
          .isSameInstanceAs(result);
    }

    @Test
    public void when_value_is_not_present() {
      var error = Try.error("original error");
      assertThat(error.mapError(m -> "error"))
          .isEqualTo(Try.error("error"));
    }
  }

  @Nested
  class _validate {
    @Test
    public void when_value_is_present() {
      var result = Try.result(7);
      assertThat(result.validate(v -> v.equals(7) ? "error" : null))
          .isEqualTo(Try.error("error"));
    }

    @Test
    public void when_value_is_not_present() {
      var error = Try.error("original error");
      assertThat(error.validate(v -> "other error"))
          .isEqualTo(Try.error("original error"));
    }
  }

  @Nested
  class _to_string {
    @Test
    public void error_to_string() {
      assertThat(Try.error("my message").toString())
          .isEqualTo("Try.error(\"my message\")");
    }

    @Test
    public void result_to_string() {
      assertThat(Try.result(37).toString())
          .isEqualTo("Try.result(37)");
    }
  }
}
