package org.smoothbuild.util.collect;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LabeledTest {
  @Test
  public void ctor_throws_exception_when_object_is_null() {
    assertCall(() -> new Labeled<Integer>(Optional.of("name"), null))
      .throwsException(NullPointerException.class);
  }

  @Test
  public void ctor_throws_exception_when_name_is_null() {
    assertCall(() -> new Labeled<>(null, 7))
      .throwsException(NullPointerException.class);
  }

  @Nested
  class _quoted_name {
    @Test
    public void of_object_with_name() {
      var named = Labeled.labeled("my_name", 7);
      assertThat(named.q())
          .isEqualTo("`my_name`");
    }

    @Test
    public void of_object_without_name() {
      var named = Labeled.labeled(7);
      assertThat(named.q())
          .isEqualTo("``");
    }
  }

  @Nested
  class _sane_name {
    @Test
    public void of_object_with_name() {
      var named = Labeled.labeled("my_name", 7);
      assertThat(named.saneLabel())
          .isEqualTo("my_name");
    }

    @Test
    public void of_object_without_name() {
      var named = Labeled.labeled(7);
      assertThat(named.saneLabel())
          .isEqualTo("");
    }
  }
}
