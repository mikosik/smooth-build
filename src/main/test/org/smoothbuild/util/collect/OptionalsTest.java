package org.smoothbuild.util.collect;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class OptionalsTest {
  @Nested
  class _pull_up_iterable {
    @Test
    public void iterable_with_zero_elems() {
      assertThat(pullUp(list()))
          .isEqualTo(Optional.of(list()));
    }

    @Test
    public void iterable_with_empty_optional() {
      assertThat(pullUp(list(Optional.of("abc"), Optional.empty())))
          .isEqualTo(Optional.empty());
    }

    @Test
    public void iterable_with_all_elems_present() {
      assertThat(pullUp(list(Optional.of("abc"), Optional.of("def"))))
          .isEqualTo(Optional.of(list("abc", "def")));
    }
  }

  @Nested
  class _pull_up_map {
    @Test
    public void map_with_zero_elems() {
      assertThat(pullUp(Map.of()))
          .isEqualTo(Optional.of(Map.of()));
    }

    @Test
    public void map_with_empty_optional() {
      assertThat(pullUp(Map.of("key1", Optional.of("abc"), "key2", Optional.empty())))
          .isEqualTo(Optional.empty());
    }

    @Test
    public void map_with_all_elems_present() {
      assertThat(pullUp(Map.of("key1", Optional.of("abc"), "key2", Optional.of("def"))))
          .isEqualTo(Optional.of(Map.of("key1", "abc", "key2", "def")));
    }
  }
}
