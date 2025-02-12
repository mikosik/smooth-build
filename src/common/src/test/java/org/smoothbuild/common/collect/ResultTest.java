package org.smoothbuild.common.collect;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Locale.ROOT;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.common.collect.Result.err;
import static org.smoothbuild.common.collect.Result.ok;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.io.IOException;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.Result.Ok;
import org.smoothbuild.common.function.Consumer1;

public class ResultTest {
  @Nested
  class _ok {
    @Test
    void ok_returns_value() {
      assertThat(ok("a").ok()).isEqualTo("a");
    }

    @Test
    void err_fails() {
      assertCall(() -> ok("a").err()).throwsException(NoSuchElementException.class);
    }

    @Test
    void isOk_returns_true_for_ok() {
      assertThat(ok("a").isOk()).isTrue();
    }

    @Test
    void isErr_returns_false_for_ok() {
      assertThat(ok("a").isErr()).isFalse();
    }

    @Test
    void ifOk_calls_consumer() {
      Consumer1<String, RuntimeException> consumer = mock();
      ok("a").ifOk(consumer);
      verify(consumer).accept("a");
    }

    @Test
    void ifOk_returns_same_instance() {
      var ok = ok("a");
      assertThat(ok.ifOk(x -> {})).isSameInstanceAs(ok);
    }

    @Test
    void ifOk_propagates_exception_from_consumer() {
      var exception = new Exception("message");
      var ok = ok("a");
      assertCall(() -> ok.ifOk((x) -> {
            throw exception;
          }))
          .throwsException(exception);
    }

    @Test
    void ifErr_not_calls_consumer() {
      Consumer1<String, RuntimeException> consumer = mock();
      Ok<String> ok = ok("a");
      ok.ifErr(consumer);
      verifyNoInteractions(consumer);
    }

    @Test
    void ifErr_returns_same_instance() {
      var ok = ok("a");
      assertThat(ok.ifErr(x -> {})).isSameInstanceAs(ok);
    }

    @Test
    void okOr_returns_ok() {
      assertThat(ok("a").okOr("b")).isEqualTo("a");
    }

    @Test
    void okOrGet_returns_ok() {
      assertThat(ok("a").okOrGet(() -> "b")).isEqualTo("a");
    }

    @Test
    void errOrGet_returns_supplied_value() {
      assertThat(ok("a").errOrGet(() -> "b")).isEqualTo("b");
    }

    @Test
    void errOrGet_propagates_exception_from_supplier() {
      var exception = new Exception("message");
      assertCall(() -> ok("a").errOrGet(() -> {
            throw exception;
          }))
          .throwsException(exception);
    }

    @Test
    void okOrThrow_returns_ok() {
      assertThat(ok("a").okOrThrow(r -> new RuntimeException("bug"))).isEqualTo("a");
    }

    @Test
    void mapOk_converts_ok() {
      assertThat(ok("a").mapOk(s -> s.toUpperCase(ROOT))).isEqualTo(ok("A"));
    }

    @Test
    void mapOk_propagates_exception_from_mapper() {
      var exception = new Exception();
      assertCall(() -> ok("a").mapOk(s -> {
            throw exception;
          }))
          .throwsException(exception);
    }

    @Test
    void mapErr_returns_same_instance() {
      var ok = ok("a");
      assertThat(ok.mapErr(x -> null)).isSameInstanceAs(ok);
    }

    @Test
    void mapErr_not_calls_mapper() {
      assertCall(() -> ok("a").mapErr(s -> {
        throw new Exception();
      }));
    }

    @Test
    void flatMapOk_converts_ok() {
      assertThat(ok("a").flatMapOk(s -> ok(s.toUpperCase(ROOT)))).isEqualTo(ok("A"));
    }

    @Test
    void flatMapOk_fails_when_mapper_returns_null() {
      var ok = ok("a");
      assertCall(() -> ok.flatMapOk(s -> null)).throwsException(NullPointerException.class);
    }

    @Test
    void flatMapOk_propagates_exception_from_mapper() {
      var exception = new Exception();
      assertCall(() -> ok("a").flatMapOk(s -> {
            throw exception;
          }))
          .throwsException(exception);
    }

    @Test
    void flatMapErr_returns_same_instance() {
      var ok = ok("a");
      assertThat(ok.flatMapErr(x -> null)).isSameInstanceAs(ok);
    }

    @Test
    void flatMapErr_not_calls_mapper() {
      assertCall(() -> ok("a").flatMapErr(s -> {
        throw new Exception();
      }));
    }
  }

  @Nested
  class _err {
    @Test
    void ok_fails() {
      assertCall(() -> err("a").ok()).throwsException(NoSuchElementException.class);
    }

    @Test
    void err_returns_value() {
      assertThat(err("a").err()).isEqualTo("a");
    }

    @Test
    void isOk_returns_false_for_err() {
      assertThat(err("a").isOk()).isFalse();
    }

    @Test
    void isErr_returns_true_for_err() {
      assertThat(err("a").isErr()).isTrue();
    }

    @Test
    void ifOk_not_calls_consumer() {
      Consumer1<String, RuntimeException> consumer = mock();
      Result<String> ok = err("a");
      ok.ifOk(consumer);
      verifyNoInteractions(consumer);
    }

    @Test
    void ifOk_returns_same_instance() {
      var err = err("a");
      assertThat(err.ifOk(x -> {})).isSameInstanceAs(err);
    }

    @Test
    void ifErr_calls_consumer() {
      Consumer1<String, RuntimeException> consumer = mock();
      err("a").ifErr(consumer);
      verify(consumer).accept("a");
    }

    @Test
    void ifErr_returns_same_instance() {
      var err = err("a");
      assertThat(err.ifErr(x -> {})).isSameInstanceAs(err);
    }

    @Test
    void ifErr_propagates_exception_from_consumer() {
      var exception = new Exception("message");
      var err = err("a");
      assertCall(() -> err.ifErr((x) -> {
            throw exception;
          }))
          .throwsException(exception);
    }

    @Test
    void okOr_returns_supplied_value() {
      assertThat(err("a").okOr("b")).isEqualTo("b");
    }

    @Test
    void okOrGet_returns_supplied_value() {
      assertThat(err("a").okOrGet(() -> "b")).isEqualTo("b");
    }

    @Test
    void okOrGet_propagates_exception_from_supplier() {
      var exception = new Exception("message");
      assertCall(() -> err("a").okOrGet(() -> {
            throw exception;
          }))
          .throwsException(exception);
    }

    @Test
    void errOrGet_returns_err() {
      assertThat(err("a").errOrGet(() -> "b")).isEqualTo("a");
    }

    @Test
    void okOrThrow_throws_exception() {
      assertCall(() -> err("message").okOrThrow(IOException::new))
          .throwsException(new IOException("message"));
    }

    @Test
    void mapOk_returns_same_err() {
      var ok = err("a");
      assertThat(ok.mapOk(x -> null)).isEqualTo(err("a"));
    }

    @Test
    void mapOk_not_calls_mapper() {
      assertCall(() -> err("a").mapOk(s -> {
        throw new Exception();
      }));
    }

    @Test
    void mapErr_converts_ok() {
      assertThat(err("a").mapErr(s -> s.toUpperCase(ROOT))).isEqualTo(err("A"));
    }

    @Test
    void mapErr_propagates_exception_from_mapper() {
      var exception = new Exception();
      assertCall(() -> err("a").mapErr(s -> {
            throw exception;
          }))
          .throwsException(exception);
    }

    @Test
    void flatMapOk_returns_same_instance() {
      var ok = err("a");
      assertThat(ok.flatMapOk(x -> null)).isEqualTo(err("a"));
    }

    @Test
    void flatMapOk_not_calls_mapper() {
      assertCall(() -> err("a").flatMapOk(s -> {
        throw new Exception();
      }));
    }

    @Test
    void flatMapErr_converts_ok() {
      assertThat(err("a").flatMapErr(s -> err(s.toUpperCase(ROOT)))).isEqualTo(err("A"));
    }

    @Test
    void flatMapErr_fails_when_mapper_returns_null() {
      var err = err("a");
      assertCall(() -> err.flatMapErr(s -> null)).throwsException(NullPointerException.class);
    }

    @Test
    void flatMapErr_propagates_exception_from_mapper() {
      var exception = new Exception();
      assertCall(() -> err("a").flatMapErr(s -> {
            throw exception;
          }))
          .throwsException(exception);
    }
  }
}
