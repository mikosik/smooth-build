package org.smoothbuild.common.collect;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Locale.ROOT;
import static org.smoothbuild.common.collect.Either.left;
import static org.smoothbuild.common.collect.Either.right;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class EitherTest {
  @Nested
  class _right {
    @Test
    void isRight_returns_true_for_right() {
      assertThat(right("a").isRight()).isTrue();
    }

    @Test
    void isLeft_returns_false_for_right() {
      assertThat(right("a").isLeft()).isFalse();
    }

    @Test
    void right_returns_value() {
      assertThat(right("a").right()).isEqualTo("a");
    }

    @Test
    void left_fails() {
      assertCall(() -> right("a").left()).throwsException(NoSuchElementException.class);
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
    void isRight_returns_false_for_left() {
      assertThat(left("a").isRight()).isFalse();
    }

    @Test
    void isLeft_returns_true_for_left() {
      assertThat(left("a").isLeft()).isTrue();
    }

    @Test
    void right_fails() {
      assertCall(() -> left("a").right()).throwsException(NoSuchElementException.class);
    }

    @Test
    void left_returns_value() {
      assertThat(left("a").left()).isEqualTo("a");
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
