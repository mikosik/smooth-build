package org.smoothbuild.util.collect;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Optional.empty;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Optionals.flatMapPair;
import static org.smoothbuild.util.collect.Optionals.mapPair;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class OptionalsTest {
  private static final BiFunction<Boolean, Integer, String> concatBoolAndInt =
      (Boolean a, Integer b) -> Boolean.toString(a) + b;
  private static final BiFunction<Boolean, Integer, Optional<String>> concatBoolAndIntOpt =
      (Boolean a, Integer b) -> Optional.of(Boolean.toString(a) + b);

  @Nested
  class _map_pair {
    @Test
    public void both_empty() {
      assertThat(mapPair(empty(), empty(), concatBoolAndInt))
          .isEqualTo(empty());
    }

    @Test
    public void first_empty() {
      assertThat(mapPair(empty(), Optional.of(7), concatBoolAndInt))
          .isEqualTo(empty());
    }

    @Test
    public void second_empty() {
      assertThat(mapPair(Optional.of(true), empty(), concatBoolAndInt))
          .isEqualTo(empty());
    }

    @Test
    public void both_present() {
      assertThat(mapPair(Optional.of(true), Optional.of(7), concatBoolAndInt))
          .isEqualTo(Optional.of("true7"));
    }
  }

  @Nested
  class _flat_map_pair {
    @Test
    public void both_empty() {
      assertThat(flatMapPair(empty(), empty(), concatBoolAndIntOpt))
          .isEqualTo(empty());
    }

    @Test
    public void first_empty() {
      assertThat(flatMapPair(empty(), Optional.of(7), concatBoolAndIntOpt))
          .isEqualTo(empty());
    }

    @Test
    public void second_empty() {
      assertThat(flatMapPair(Optional.of(true), empty(), concatBoolAndIntOpt))
          .isEqualTo(empty());
    }

    @Test
    public void both_present() {
      assertThat(flatMapPair(Optional.of(true), Optional.of(7), concatBoolAndIntOpt))
          .isEqualTo(Optional.of("true7"));
    }
  }

  @Nested
  class _pull_up_iterable {
    @Test
    public void iterable_with_zero_elems() {
      assertThat(pullUp(list()))
          .isEqualTo(Optional.of(list()));
    }

    @Test
    public void iterable_with_empty_optional() {
      assertThat(pullUp(list(Optional.of("abc"), empty())))
          .isEqualTo(empty());
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
      assertThat(pullUp(Map.of("key1", Optional.of("abc"), "key2", empty())))
          .isEqualTo(empty());
    }

    @Test
    public void map_with_all_elems_present() {
      assertThat(pullUp(Map.of("key1", Optional.of("abc"), "key2", Optional.of("def"))))
          .isEqualTo(Optional.of(Map.of("key1", "abc", "key2", "def")));
    }
  }
}
