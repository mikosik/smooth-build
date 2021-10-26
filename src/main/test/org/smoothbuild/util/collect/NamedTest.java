package org.smoothbuild.util.collect;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Named.named;

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class NamedTest {
  @Test
  public void constructor_throws_exception_when_object_is_null() {
    assertCall(() -> new Named<Integer>(Optional.of("name"), null))
      .throwsException(NullPointerException.class);
  }

  @Test
  public void constructor_throws_exception_when_name_is_null() {
    assertCall(() -> new Named<>(null, 7))
      .throwsException(NullPointerException.class);
  }

  @Nested
  class _quoted_name {
    @Test
    public void of_object_with_name() {
      var named = named("my_name", 7);
      assertThat(named.q())
          .isEqualTo("`my_name`");
    }

    @Test
    public void of_object_without_name() {
      var named = named(7);
      assertThat(named.q())
          .isEqualTo("``");
    }
  }

  @Nested
  class _sane_name {
    @Test
    public void of_object_with_name() {
      var named = named("my_name", 7);
      assertThat(named.saneName())
          .isEqualTo("my_name");
    }

    @Test
    public void of_object_without_name() {
      var named = named(7);
      assertThat(named.saneName())
          .isEqualTo("");
    }
  }
}
