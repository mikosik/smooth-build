package org.smoothbuild.cli.console;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class MaybeTest {
  private Maybe<String> value;

  @BeforeEach
  public void before() {
    value = new Maybe<>();
  }

  @Test
  public void of_value() {
    var maybe = Maybe.of("abc");
    assertThat(maybe.value())
        .isEqualTo("abc");
  }

  @Test
  public void with_logs_from() {
    MemoryLogger logger = new MemoryLogger();
    logger.error("message");
    var maybe = Maybe.withLogsFrom(logger);
    assertThat(maybe.logs())
        .isEqualTo(logger.logs());
  }

  @Nested
  class value {
    @Test
    public void is_initially_null() {
      assertThat(value.value())
          .isNull();
    }

    @Test
    public void returns_previously_set_value() {
      value.setValue("abc");
      assertThat(value.value())
          .isEqualTo("abc");
    }
  }

  @Test
  public void test_equals_and_hashcode() {
    new EqualsTester()
        .addEqualityGroup(new Maybe<>(), new Maybe<>())
        .addEqualityGroup(Maybe.of("abc"), Maybe.of("abc"))
        .addEqualityGroup(Maybe.of("def"), Maybe.of("def"))
        .addEqualityGroup(withLog("abc"), withLog("abc"))
        .addEqualityGroup(withLog("def"), withLog("def"));
  }

  @Test
  public void to_string() {
    Maybe<String> maybe = Maybe.of("abc");
    maybe.error("message");
    assertThat(maybe.toString())
        .isEqualTo("Maybe{abc, [Log{ERROR, 'message'}]}");
  }

  private static Maybe<String> withLog(String message) {
    Maybe<String> maybe = new Maybe<>();
    maybe.error(message);
    return maybe;
  }
}
