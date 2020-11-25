package org.smoothbuild.cli.console;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MaybeTest {
  private Maybe<String> value;

  @BeforeEach
  public void before() {
    value = new Maybe<>();
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
}
