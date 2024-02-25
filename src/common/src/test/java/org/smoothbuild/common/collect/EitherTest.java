package org.smoothbuild.common.collect;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Locale.ROOT;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.common.collect.Either.left;
import static org.smoothbuild.common.collect.Either.right;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.Either.Right;
import org.smoothbuild.common.function.Consumer1;

public class EitherTest {
  @Nested
  class _right {
    @Test
    void right_returns_value() {
      assertThat(right("a").right()).isEqualTo("a");
    }

    @Test
    void left_fails() {
      assertCall(() -> right("a").left()).throwsException(NoSuchElementException.class);
    }

    @Test
    void isRight_returns_true_for_right() {
      assertThat(right("a").isRight()).isTrue();
    }

    @Test
    void isLeft_returns_false_for_right() {
      assertThat(right("a").isLeft()).isFalse();
    }

    @Test
    void ifRight_calls_consumer() {
      Consumer1<String, RuntimeException> consumer = mock();
      right("a").ifRight(consumer);
      verify(consumer).accept("a");
    }

    @Test
    void ifRight_returns_same_instance() {
      var right = right("a");
      assertThat(right.ifRight(x -> {})).isSameInstanceAs(right);
    }

    @Test
    void ifRight_propagates_exception_from_consumer() {
      var exception = new Exception("message");
      var right = right("a");
      assertCall(() -> right.ifRight((x) -> {
            throw exception;
          }))
          .throwsException(exception);
    }

    @Test
    void ifLeft_not_calls_consumer() {
      Consumer1<String, RuntimeException> consumer = mock();
      Right<String, String> right = right("a");
      right.ifLeft(consumer);
      verifyNoInteractions(consumer);
    }

    @Test
    void rightOrGet_returns_right() {
      assertThat(right("a").rightOrGet(() -> "b")).isEqualTo("a");
    }

    @Test
    void leftOrGet_returns_supplied_value() {
      assertThat(right("a").leftOrGet(() -> "b")).isEqualTo("b");
    }

    @Test
    void leftOrGet_propagates_exception_from_supplier() {
      var exception = new Exception("message");
      assertCall(() -> right("a").leftOrGet(() -> {
            throw exception;
          }))
          .throwsException(exception);
    }

    @Test
    void mapRight_converts_right() {
      assertThat(right("a").mapRight(s -> s.toUpperCase(ROOT))).isEqualTo(right("A"));
    }

    @Test
    void mapRight_propagates_exception_from_mapper() {
      var exception = new Exception();
      assertCall(() -> right("a").mapRight(s -> {
            throw exception;
          }))
          .throwsException(exception);
    }

    @Test
    void mapLeft_returns_same_instance() {
      var right = right("a");
      assertThat(right.mapLeft(x -> null)).isSameInstanceAs(right);
    }

    @Test
    void mapLeft_not_calls_mapper() {
      assertCall(() -> right("a").mapLeft(s -> {
        throw new Exception();
      }));
    }

    @Test
    void flatMapRight_converts_right() {
      assertThat(right("a").flatMapRight(s -> right(s.toUpperCase(ROOT)))).isEqualTo(right("A"));
    }

    @Test
    void flatMapRight_fails_when_mapper_returns_null() {
      var right = right("a");
      assertCall(() -> right.flatMapRight(s -> null)).throwsException(NullPointerException.class);
    }

    @Test
    void flatMapRight_propagates_exception_from_mapper() {
      var exception = new Exception();
      assertCall(() -> right("a").flatMapRight(s -> {
            throw exception;
          }))
          .throwsException(exception);
    }

    @Test
    void flatMapLeft_returns_same_instance() {
      var right = right("a");
      assertThat(right.flatMapLeft(x -> null)).isSameInstanceAs(right);
    }

    @Test
    void flatMapLeft_not_calls_mapper() {
      assertCall(() -> right("a").flatMapLeft(s -> {
        throw new Exception();
      }));
    }
  }

  @Nested
  class _left {
    @Test
    void right_fails() {
      assertCall(() -> left("a").right()).throwsException(NoSuchElementException.class);
    }

    @Test
    void left_returns_value() {
      assertThat(left("a").left()).isEqualTo("a");
    }

    @Test
    void isRight_returns_false_for_left() {
      assertThat(left("a").isRight()).isFalse();
    }

    @Test
    void isLeft_returns_true_for_left() {
      assertThat(left("a").isLeft()).isTrue();
    }

    @Test
    void ifRight_not_calls_consumer() {
      Consumer1<String, RuntimeException> consumer = mock();
      Either<String, String> right = left("a");
      right.ifRight(consumer);
      verifyNoInteractions(consumer);
    }

    @Test
    void ifLeft_calls_consumer() {
      Consumer1<String, RuntimeException> consumer = mock();
      left("a").ifLeft(consumer);
      verify(consumer).accept("a");
    }

    @Test
    void ifLeft_returns_same_instance() {
      var left = left("a");
      assertThat(left.ifLeft(x -> {})).isSameInstanceAs(left);
    }

    @Test
    void ifLeft_propagates_exception_from_consumer() {
      var exception = new Exception("message");
      var left = left("a");
      assertCall(() -> left.ifLeft((x) -> {
            throw exception;
          }))
          .throwsException(exception);
    }

    @Test
    void rightOrGet_returns_supplied_value() {
      assertThat(left("a").rightOrGet(() -> "b")).isEqualTo("b");
    }

    @Test
    void rightOrGet_propagates_exception_from_supplier() {
      var exception = new Exception("message");
      assertCall(() -> left("a").rightOrGet(() -> {
            throw exception;
          }))
          .throwsException(exception);
    }

    @Test
    void leftOrGet_returns_left() {
      assertThat(left("a").leftOrGet(() -> "b")).isEqualTo("a");
    }

    @Test
    void mapRight_returns_same_instance() {
      var right = left("a");
      assertThat(right.mapRight(x -> null)).isSameInstanceAs(right);
    }

    @Test
    void mapRight_not_calls_mapper() {
      assertCall(() -> left("a").mapRight(s -> {
        throw new Exception();
      }));
    }

    @Test
    void mapLeft_converts_right() {
      assertThat(left("a").mapLeft(s -> s.toUpperCase(ROOT))).isEqualTo(left("A"));
    }

    @Test
    void mapLeft_propagates_exception_from_mapper() {
      var exception = new Exception();
      assertCall(() -> left("a").mapLeft(s -> {
            throw exception;
          }))
          .throwsException(exception);
    }

    @Test
    void flatMapRight_returns_same_instance() {
      var right = left("a");
      assertThat(right.flatMapRight(x -> null)).isSameInstanceAs(right);
    }

    @Test
    void flatMapRight_not_calls_mapper() {
      assertCall(() -> left("a").flatMapRight(s -> {
        throw new Exception();
      }));
    }

    @Test
    void flatMapLeft_converts_right() {
      assertThat(left("a").flatMapLeft(s -> left(s.toUpperCase(ROOT)))).isEqualTo(left("A"));
    }

    @Test
    void flatMapLeft_fails_when_mapper_returns_null() {
      var left = left("a");
      assertCall(() -> left.flatMapLeft(s -> null)).throwsException(NullPointerException.class);
    }

    @Test
    void flatMapLeft_propagates_exception_from_mapper() {
      var exception = new Exception();
      assertCall(() -> left("a").flatMapLeft(s -> {
            throw exception;
          }))
          .throwsException(exception);
    }
  }
}
