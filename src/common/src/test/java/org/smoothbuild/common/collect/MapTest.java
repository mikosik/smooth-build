package org.smoothbuild.common.collect;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.collect.Map.mapOfAll;
import static org.smoothbuild.common.collect.Map.zipToMap;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import com.google.common.testing.EqualsTester;
import java.util.Iterator;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class MapTest {
  @Nested
  class _factory_method {
    @Test
    void with_no_arguments() {
      var map = map();
      assertThat(map).isEqualTo(java.util.Map.of());
    }

    @Test
    void with_one_argument() {
      var map = map(1, "one");
      assertThat(map).isEqualTo(java.util.Map.of(1, "one"));
    }

    @Test
    void with_two_arguments() {
      var map = map(1, "one", 2, "two");
      assertThat(map).isEqualTo(java.util.Map.of(1, "one", 2, "two"));
    }

    @Test
    void with_three_arguments() {
      var map = map(1, "one", 2, "two", 3, "three");
      assertThat(map).isEqualTo(java.util.Map.of(1, "one", 2, "two", 3, "three"));
    }

    @Test
    void mapOfAllCopiesDataFromJavaUtilMap() {
      var javaUtilMap = java.util.Map.of(1, "one", 2, "two", 3, "three");
      var map = mapOfAll(javaUtilMap);
      assertThat(map).isEqualTo(javaUtilMap);
      assertThat(map).isNotSameInstanceAs(javaUtilMap);
    }

    @Test
    void mapOfAllReturnsArgumentIfItIsInstanceOfCustomMap() {
      var customMap = map(1, "1", 2, "2");
      var map = mapOfAll(customMap);
      assertThat(map).isSameInstanceAs(customMap);
    }
  }

  @Nested
  class _zipToMap {
    @Test
    void for_empty_iterables_returns_empty_map() {
      assertThat(zipToMap(java.util.List.of(), java.util.List.of())).isEmpty();
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
  class _modifying_map_by_calling {
    @ParameterizedTest
    @MethodSource("maps")
    void remove_fails(Map<Integer, String> map) {
      assertCall(() -> map.remove(1)).throwsException(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("maps")
    void remove2_fails(Map<Integer, String> map) {
      assertCall(() -> map.remove(1, "one")).throwsException(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("maps")
    void clear_fails(Map<Integer, String> map) {
      assertCall(map::clear).throwsException(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("maps")
    void put_fails(Map<Integer, String> map) {
      assertCall(() -> map.put(-1, "minus")).throwsException(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("maps")
    void putIfAbsent_fails(Map<Integer, String> map) {
      assertCall(() -> map.putIfAbsent(-1, "minus"))
          .throwsException(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("maps")
    void putAll_fails(Map<Integer, String> map) {
      assertCall(() -> map.putAll(java.util.Map.of(-1, "-1")))
          .throwsException(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("maps")
    void replace_fails(Map<Integer, String> map) {
      assertCall(() -> map.replace(1, "1")).throwsException(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("maps")
    void replace2_fails(Map<Integer, String> map) {
      assertCall(() -> map.replace(1, "one", "1"))
          .throwsException(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("maps")
    void replaceAll_fails(Map<Integer, String> map) {
      assertCall(() -> map.replaceAll((k, v) -> v))
          .throwsException(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("maps")
    void merge_fails(Map<Integer, String> map) {
      assertCall(() -> map.merge(1, "one", (s1, s2) -> s1 + s2))
          .throwsException(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("maps")
    void modifying_entrySet_fails(Map<Integer, String> map) {
      assertCall(() -> map.entrySet().clear()).throwsException(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("maps")
    void compute_fails(Map<Integer, String> map) {
      assertCall(() -> map.compute(1, (k, v) -> v))
          .throwsException(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("maps")
    void computeIfAbsent_fails(Map<Integer, String> map) {
      assertCall(() -> map.computeIfAbsent(7, k -> "7"))
          .throwsException(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("maps")
    void computeIfPresent_fails(Map<Integer, String> map) {
      assertCall(() -> map.computeIfPresent(1, (k, v) -> v))
          .throwsException(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("maps")
    void entrySet_iterator_remove_fails(Map<Integer, String> map) {
      var iterator = map.entrySet().iterator();
      if (iterator.hasNext()) {
        iterator.next();
        assertCall(iterator::remove).throwsException(UnsupportedOperationException.class);
      }
    }

    @ParameterizedTest
    @MethodSource("maps")
    void values_iterator_remove_fails(Map<Integer, String> map) {
      Iterator<String> iterator = map.values().iterator();
      if (iterator.hasNext()) {
        iterator.next();
        assertCall(iterator::remove).throwsException(UnsupportedOperationException.class);
      }
    }

    @ParameterizedTest
    @MethodSource("maps")
    void keys_iterator_remove_fails(Map<Integer, String> map) {
      Iterator<Integer> iterator = map.keySet().iterator();
      if (iterator.hasNext()) {
        iterator.next();
        assertCall(iterator::remove).throwsException(UnsupportedOperationException.class);
      }
    }

    public static Stream<Arguments> maps() {
      return Stream.of(
          arguments(map()),
          arguments(map(1, "one")),
          arguments(map(1, "one", 2, "two")),
          arguments(map(1, "one", 2, "two", 3, "three")),
          arguments(map(1, "one", 2, "two", 3, "three", 4, "four")),
          arguments(mapOfAll(java.util.Map.of(1, "one", 2, "two"))),
          arguments(map(1, "one").mapValues(String::toUpperCase)));
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
      assertThat(map().overrideWith(java.util.Map.of())).isEqualTo(java.util.Map.of());
    }

    @Test
    void empty_overridden_with_non_empty() {
      assertThat(map().overrideWith(java.util.Map.of(1, "one"))).isEqualTo(map(1, "one"));
    }

    @Test
    void non_empty_overridden_with_empty() {
      assertThat(map(1, "one").overrideWith(java.util.Map.of())).isEqualTo(map(1, "one"));
    }

    @Test
    void map_overridden_with_map_without_overlapping_keys() {
      assertThat(map(1, "one").overrideWith(java.util.Map.of(2, "two")))
          .isEqualTo(map(1, "one", 2, "two"));
    }

    @Test
    void map_overridden_with_map_with_overlapping_keys_take_entries_from_overriding() {
      assertThat(map(1, "one", 2, "two").overrideWith(java.util.Map.of(2, "TWO")))
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
