package org.smoothbuild.util.collect;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ResultTest {

  @Nested
  class _factory_method {
    @Test
    public void of_throws_exception_when_value_is_null() {
      assertCall(() -> Result.of(null))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void error_throws_exception_when_error_is_null() {
      assertCall(() -> Result.error(null))
          .throwsException(IllegalArgumentException.class);
    }
  }

  @Nested
  class _value {
    @Test
    public void return_value() {
      var result = Result.of(7);
      assertThat(result.value())
          .isEqualTo(7);
    }

    @Test
    public void throws_exception_when_value_is_not_present() {
      var result = Result.error("error");
      assertCall(result::value)
          .throwsException(IllegalStateException.class);
    }
  }

  @Nested
  class _error {
    @Test
    public void return_error() {
      var result = Result.error("error");
      assertThat(result.error())
          .isEqualTo("error");
    }

    @Test
    public void throws_exception_when_value_is_present() {
      var result = Result.of(7);
      assertCall(result::error)
          .throwsException(IllegalStateException.class);
    }
  }

  @Nested
  class _is_present {
    @Test
    public void return_true_when_value_is_present() {
      var result = Result.of(7);
      assertThat(result.isPresent())
          .isTrue();
    }

    @Test
    public void return_false_when_error_is_present() {
      var result = Result.error("error");
      assertThat(result.isPresent())
          .isFalse();
    }
  }

  @Nested
  class _orElse {
    @Test
    public void when_value_is_present() {
      var result = Result.of(7);
      assertThat(result.orElse(e -> 33))
          .isEqualTo(7);
    }

    @Test
    public void when_value_is_not_present() {
      var result = Result.error("some error");
      assertThat(result.orElse(e -> e + "!"))
          .isEqualTo("some error!");
    }
  }

  @Nested
  class _map {
    @Test
    public void mapping_value() {
      var result = Result.of(7);
      assertThat(result.map(Object::toString))
          .isEqualTo(Result.of("7"));
    }

    @Test
    public void mapping_error() {
      var result = Result.error("error");
      assertThat(result.map(Object::toString))
          .isSameInstanceAs(result);
    }
  }

  @Nested
  class _flatMap {
    @Test
    public void mapping_value_to_value() {
      var result = Result.of(7);
      assertThat(result.flatMap(v -> Result.of(v.toString())))
          .isEqualTo(Result.of("7"));
    }

    @Test
    public void mapping_value_to_error() {
      var result = Result.of(7);
      assertThat(result.flatMap(v -> Result.error("error")))
          .isEqualTo(Result.error("error"));
    }

    @Test
    public void mapping_error() {
      var result = Result.error("error");
      assertThat(result.flatMap(v -> Result.of(v.toString())))
          .isSameInstanceAs(result);
    }
  }

  @Nested
  class _mapError {
    @Test
    public void when_value_is_present() {
      var result = Result.of(7);
      assertThat(result.mapError(m -> "error"))
          .isSameInstanceAs(result);
    }

    @Test
    public void when_value_is_not_present() {
      var result = Result.error("original error");
      assertThat(result.mapError(m -> "error"))
          .isEqualTo(Result.error("error"));
    }
  }

  @Nested
  class _validate {
    @Test
    public void when_value_is_present() {
      var result = Result.of(7);
      assertThat(result.validate(v -> v.equals(7) ? "error" : null))
          .isEqualTo(Result.error("error"));
    }

    @Test
    public void when_value_is_not_present() {
      var result = Result.error("original error");
      assertThat(result.validate(v -> "other error"))
          .isEqualTo(Result.error("original error"));
    }
  }
}
