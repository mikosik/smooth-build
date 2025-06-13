package org.smoothbuild.common.collect;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.collect.Map.mapOfAll;
import static org.smoothbuild.common.collect.Map.zipToMap;
import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MapTest {
  @Nested
  class _mapOfAll {
    @Test
    void with_empty_map() {
      var map = mapOfAll(java.util.Map.of());
      assertThat(map).isEqualTo(map());
    }

    @Test
    void with_non_empty_map() {
      var map = mapOfAll(java.util.Map.of(1, "one", 2, "two"));
      assertThat(map).isEqualTo(map(1, "one", 2, "two"));
    }
  }

  @Nested
  class _entrySet {
    @Test
    void empty() {
      Map<Integer, String> map = map();
      assertThat(map.entrySet()).isEqualTo(set());
    }

    @Test
    void non_empty() {
      var map = map(1, "one", 2, "two");
      var expectedSet = set(java.util.Map.entry(1, "one"), java.util.Map.entry(2, "two"));
      assertThat(map.entrySet()).isEqualTo(expectedSet);
    }
  }

  @Nested
  class _keySet {
    @Test
    void empty() {
      Map<Integer, String> map = map();
      assertThat(map.keySet()).isEqualTo(set());
    }

    @Test
    void non_empty() {
      var map = map(1, "one", 2, "two");
      assertThat(map.keySet()).isEqualTo(set(1, 2));
    }
  }

  @Nested
  class _values {
    @Test
    void empty() {
      var map = map();
      assertThat(map.values()).isEmpty();
    }

    @Test
    void no_empty() {
      var map = map(1, "one", 2, "two");
      assertThat(map.values()).containsExactly("one", "two");
    }
  }

  @Nested
  class _put {
    @Test
    void empty_map_put_returns_new_map() {
      var map = map();
      assertThat(map.put(1, "one")).isEqualTo(map(1, "one"));
    }

    @Test
    void non_empty_map_put_returns_new_map_with_new_entry() {
      var map = map(1, "one");
      assertThat(map.put(2, "two")).isEqualTo(map(1, "one", 2, "two"));
    }

    @Test
    void returns_map_with_new_mapping_when_key_already_exists() {
      var map = map(1, "one");
      assertThat(map.put(1, "two")).isEqualTo(map(1, "two"));
    }
  }

  @Nested
  class _zipToMap {
    @Test
    void for_empty_iterables_returns_empty_map() {
      var map = zipToMap(java.util.List.of(), java.util.List.of());
      assertThat(map).isEqualTo(map());
    }

    @Test
    void returns_map_zipping_iterables_into_entries() {
      assertThat(zipToMap(java.util.List.of(1, 2, 3), java.util.List.of("1", "2", "3")))
          .isEqualTo(map(1, "1", 2, "2", 3, "3"));
    }

    @Test
    void fails_when_keys_have_more_elements_than_values() {
      assertCall(() -> zipToMap(java.util.List.of(1, 2, 3), java.util.List.of("1", "2")))
          .throwsException(new IllegalArgumentException("keys have more elements than values"));
    }

    @Test
    void fails_when_values_have_more_elements_than_keys() {
      assertCall(() -> zipToMap(java.util.List.of(1, 2), java.util.List.of("1", "2", "3")))
          .throwsException(new IllegalArgumentException("values have more elements than keys"));
    }
  }

  @Nested
  class _map_keys {
    @Test
    void empty() {
      Map<Integer, String> map = map();
      assertThat(map.mapKeys(i -> i + 1)).isEqualTo(map());
    }

    @Test
    void non_empty() {
      var map = map(1, "one", 2, "two");
      assertThat(map.mapKeys(i -> i + 1)).isEqualTo(map(2, "one", 3, "two"));
    }

    @Test
    void mapping_two_keys_to_be_equal_fails() {
      var map = map(1, "one", 2, "two");
      assertCall(() -> assertThat(map.mapKeys(i -> 1)))
          .throwsException(
              new IllegalArgumentException("Multiple entries with same key: 1=two and 1=one"));
    }

    @Test
    void exception_from_mapper_is_propagated() {
      var map = map(1, "one", 2, "two");
      var exception = new Exception("message");
      assertCall(() -> map.mapKeys(s -> {
            throw exception;
          }))
          .throwsException(exception);
    }
  }

  @Nested
  class _map_values {
    @Test
    void empty() {
      Map<Integer, String> map = map();
      assertThat(map.mapValues(String::toUpperCase)).isEqualTo(map());
    }

    @Test
    void non_empty() {
      var map = map(1, "one", 2, "two");
      assertThat(map.mapValues(String::toUpperCase)).isEqualTo(map(1, "ONE", 2, "TWO"));
    }

    @Test
    void exception_from_mapper_is_propagated() {
      var map = map(1, "one", 2, "two");
      var exception = new Exception("message");
      assertCall(() -> map.mapValues(s -> {
            throw exception;
          }))
          .throwsException(exception);
    }
  }

  @Nested
  class _map_entries {
    @Test
    void empty() {
      Map<Integer, String> map = map();
      assertThat(map.mapEntries(i -> i + 1, String::toUpperCase)).isEqualTo(map());
    }

    @Test
    void non_empty() {
      var map = map(1, "one", 2, "two");
      assertThat(map.mapEntries(i -> i + 1, String::toUpperCase))
          .isEqualTo(map(2, "ONE", 3, "TWO"));
    }

    @Test
    void mapping_two_keys_to_be_equal_fails() {
      var map = map(1, "one", 2, "two");
      assertCall(() -> assertThat(map.mapEntries(i -> 1, String::toUpperCase)))
          .throwsException(
              new IllegalArgumentException("Multiple entries with same key: 1=TWO and 1=ONE"));
    }

    @Test
    void exception_from_key_mapper_is_propagated() {
      var map = map(1, "one", 2, "two");
      var exception = new Exception("message");
      assertCall(() -> map.mapEntries(
              i -> {
                throw exception;
              },
              String::toUpperCase))
          .throwsException(exception);
    }

    @Test
    void exception_from_value_mapper_is_propagated() {
      var map = map(1, "one", 2, "two");
      var exception = new Exception("message");
      assertCall(() -> map.mapEntries(i -> i + 1, s -> {
            throw exception;
          }))
          .throwsException(exception);
    }
  }

  @Nested
  class _overrideWith {
    @Test
    void empty_overridden_with_empty() {
      assertThat(map().overrideWith(map())).isEqualTo(map());
    }

    @Test
    void empty_overridden_with_non_empty() {
      assertThat(map().overrideWith(map(1, "one"))).isEqualTo(map(1, "one"));
    }

    @Test
    void non_empty_overridden_with_empty() {
      assertThat(map(1, "one").overrideWith(map())).isEqualTo(map(1, "one"));
    }

    @Test
    void map_overridden_with_map_without_overlapping_keys() {
      assertThat(map(1, "one").overrideWith(map(2, "two"))).isEqualTo(map(1, "one", 2, "two"));
    }

    @Test
    void map_overridden_with_map_with_overlapping_keys_take_entries_from_overriding() {
      assertThat(map(1, "one", 2, "two").overrideWith(map(2, "TWO")))
          .isEqualTo(map(1, "one", 2, "TWO"));
    }
  }

  @Nested
  class _to_string {
    @Test
    void argless() {
      assertThat(map(1, "A", 2, "B").toString()).isEqualTo("{1=A, 2=B}");
    }

    @Test
    void with_delimiter() {
      assertThat(map(1, "A", 2, "B").toString(":")).isEqualTo("1=A:2=B");
    }

    @Test
    void with_prefix_delimiter_suffix() {
      assertThat(map(1, "A", 2, "B").toString("{", ":", "}")).isEqualTo("{1=A:2=B}");
    }
  }

  @Test
  void equals_and_hashcode_test() {
    new EqualsTester()
        .addEqualityGroup(list(), list())
        .addEqualityGroup(list("a"), list("a"))
        .addEqualityGroup(list("x"), List.<Object>list("x"))
        .addEqualityGroup(list("a", "b"), list("a", "b"))
        .addEqualityGroup(list("a", "b", "c"), list("a", "b", "c"))
        .testEquals();
  }
}
