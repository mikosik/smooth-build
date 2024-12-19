package org.smoothbuild.common.collect;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static java.util.Comparator.naturalOrder;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.common.collect.Set.setOfAll;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import com.google.common.testing.EqualsTester;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class SetTest {
  @Nested
  class _set {
    @Test
    void with_no_arguments() {
      var set = set();
      assertThat(set).isEqualTo(set());
    }

    @Test
    void with_one_argument() {
      var set = set(1);
      assertThat(set).isEqualTo(set(1));
    }

    @Test
    void with_two_arguments() {
      var set = set(1, 2);
      assertThat(set).isEqualTo(set(1, 2));
    }

    @Test
    void removes_duplicates() {
      var set = set(1, 2, 1);
      assertThat(set).isEqualTo(set(1, 2));
    }
  }

  @Nested
  class _setOfAll {
    @Test
    void with_custom_set_returns_same_instance() {
      var set = set(1, 2, 3);
      assertThat(setOfAll(set)).isSameInstanceAs(set);
    }

    @Test
    void with_custom_list_returns_set_with_duplicates_removed() {
      var list = list(1, 2, 3, 2);
      assertThat(setOfAll(list)).isEqualTo(set(1, 2, 3));
    }

    @Test
    void with_jdk_set() {
      var jdkSet = java.util.Set.of(1, 2, 3);
      assertThat(setOfAll(jdkSet)).isEqualTo(set(1, 2, 3));
    }

    @Test
    void with_jdk_list_returns_set_with_duplicates_removed() {
      var jdkList = asList(1, 2, 3, 2);
      assertThat(setOfAll(jdkList)).isEqualTo(set(1, 2, 3));
    }
  }

  @Nested
  class _unionWith {
    @Test
    void empty_set_union_with_empty() {
      var set = set();
      assertThat(set.unionWith(java.util.List.of())).isEmpty();
    }

    @Test
    void empty_set_union_with_elements() {
      var set = set();
      assertThat(set.unionWith(java.util.List.of(1, 2))).containsExactly(1, 2);
    }

    @Test
    void non_empty_set_union_with_empty() {
      var set = set(1, 2);
      assertThat(set.unionWith(java.util.List.of())).containsExactly(1, 2);
    }

    @Test
    void non_empty_set_union_with_non_empty() {
      var set = set(1, 2);
      assertThat(set.unionWith(java.util.List.of(3, 4))).containsExactly(1, 2, 3, 4);
    }
  }

  @Nested
  class _map {
    @Test
    void empty_set_returns_empty_set() {
      Set<Integer> set = set();
      assertThat(set.map(Object::toString)).containsExactly().inOrder();
    }

    @Test
    void maps_elements() {
      var set = set(1, 2, 3);
      assertThat(set.map(Object::toString)).containsExactly("1", "2", "3").inOrder();
    }

    @Test
    void propagates_exception_from_mapper() {
      var set = set(1, 2, 3);
      var exception = new Exception("message");
      assertCall(() -> set.map((e) -> {
            throw exception;
          }))
          .throwsException(exception);
    }
  }

  @Nested
  class _filter {
    @Test
    void on_empty_set_returns_empty_set() {
      var set = set();
      assertThat(set.filter(e -> !e.equals(2))).isEmpty();
    }

    @Test
    void remove_not_matching_elements() {
      var set = set(1, 2, 3, 4, 5, 6);
      assertThat(set.filter(e -> !e.equals(2))).containsExactly(1, 3, 4, 5, 6).inOrder();
    }

    @Test
    void propagates_exception_from_predicate() {
      var set = set(1, 2, 3);
      var exception = new Exception("message");
      assertCall(() -> set.filter(e -> {
            throw exception;
          }))
          .throwsException(exception);
    }
  }

  @Nested
  class _removeAll {
    @Test
    void on_empty_set_returns_empty_set() {
      var set = set();
      assertThat(set.removeAll(set(1, 2))).isEmpty();
    }

    @Test
    void removes_elements() {
      var set = set(1, 2, 3, 4, 5, 6);
      assertThat(set.removeAll(set(2, 3))).containsExactly(1, 4, 5, 6).inOrder();
    }
  }

  @Nested
  class _sort {
    @Test
    void empty_set_returns_empty_set() {
      Set<Integer> set = set();
      assertThat(set.sort(naturalOrder())).containsExactly().inOrder();
    }

    @Test
    void sorts_elements() {
      var set = set(2, 1, 3);
      assertThat(set.sort(naturalOrder())).containsExactly(1, 2, 3).inOrder();
    }
  }

  @Nested
  class _toList {
    @Test
    void on_empty_set_returns_empty_list() {
      var set = set();
      assertThat(set.toList()).isEmpty();
    }

    @Test
    void on_non_empty_set_returns_list_with_all_elements() {
      var set = set(1, 2, 3);
      assertThat(set.toList()).containsExactly(1, 2, 3).inOrder();
    }

    @Test
    void returns_instance_of_list() {
      var set = set(1, 2, 3);
      assertThat(set.toList()).isInstanceOf(List.class);
    }
  }

  @Nested
  class _toSet {
    @Test
    void returns_same_instance() {
      var set = set(1, 2, 3);
      assertThat(set.toSet()).isSameInstanceAs(set);
    }
  }

  @Nested
  class _toMap {
    @Test
    void on_empty_set_returns_empty_map() {
      var set = set();
      assertThat(set.toMap(x -> x)).isEmpty();
    }

    @Test
    void on_non_empty_set_returns_map_with_values_calculated_by_mapper() {
      var set = set(1, 2, 3);
      assertThat(set.toMap(Object::toString))
          .containsExactly(1, "1", 2, "2", 3, "3")
          .inOrder();
    }

    @Test
    void propagates_exception_from_mapper() {
      var set = set(1, 2, 3);
      var exception = new RuntimeException("message");
      assertCall(() -> set.toMap(x -> {
            throw exception;
          }))
          .throwsException(exception);
    }

    @Test
    void returns_instance_of_map() {
      var set = set(1, 2, 3);
      assertThat(set.toMap(x -> x)).isInstanceOf(Map.class);
    }
  }

  @Nested
  class _isEmpty {
    @Test
    void returns_true_for_empty_set() {
      assertThat(set().isEmpty()).isTrue();
    }

    @Test
    void returns_false_for_non_empty_set() {
      assertThat(set(1).isEmpty()).isFalse();
    }
  }

  @Nested
  class _contains {
    @Test
    void returns_true_when_set_contains_element() {
      assertThat(set(1, 2, 3).contains(2)).isTrue();
    }

    @Test
    void returns_false_when_set_not_contains_element() {
      assertThat(set(1, 2, 3).contains(4)).isFalse();
    }
  }

  @Nested
  class _stream {
    @Test
    void provides_all_set_elements() {
      var set = set(1, 2, 3);
      assertThat(set.stream().toList()).containsExactly(1, 2, 3);
    }

    @Test
    void provides_no_elements_for_empty_set() {
      var set = set();
      assertThat(set.stream().toList()).isEmpty();
    }
  }

  @Nested
  class _toArray {
    @Test
    void returns_array_with_all_elements() {
      var set = set(1, 2, 3);
      assertThat(set.toArray()).asList().containsExactly(1, 2, 3);
    }

    @Test
    void returns_empty_array_for_empty_set() {
      var set = set();
      assertThat(set.toArray()).asList().isEmpty();
    }

    @Test
    void returns_array_which_modification_does_not_affect_set() {
      var set = set(1, 2, 3);
      var array = set.toArray();
      array[0] = 7;
      assertThat(set).isEqualTo(set(1, 2, 3));
    }
  }

  @Nested
  class _iterator {
    @Test
    void returns_iterator_with_all_elements() {
      var set = set(1, 2, 3);
      assertThat(newArrayList(set.iterator())).containsExactly(1, 2, 3);
    }

    @Test
    void returns_empty_iterator_for_empty_list() {
      var set = set();
      assertThat(newArrayList(set.iterator())).isEmpty();
    }

    @Test
    void returns_iterator_which_remove_method_fails_with_exception() {
      var set = set(1, 2, 3);
      assertCall(() -> set.iterator().remove())
          .throwsException(UnsupportedOperationException.class);
    }
  }

  @Nested
  class _collection_methods extends AbstractCollectionTestSuite {
    @SafeVarargs
    @Override
    public final <E> Collection<E> createCollection(E... elements) {
      return set(elements);
    }
  }

  @Nested
  class _size {
    @ParameterizedTest
    @MethodSource
    void of_empty_list_is_zero(Set<?> set, int expectedSize) {
      assertThat(set.size()).isEqualTo(expectedSize);
    }

    static Stream<Arguments> of_empty_list_is_zero() {
      return Stream.of(
          arguments(set(), 0),
          arguments(set(1), 1),
          arguments(set(1, 2), 2),
          arguments(set(1, 2, 3), 3),
          arguments(set(1, 2, 3, 4), 4),
          arguments(set(1, 2, 3, 2), 3));
    }
  }

  @Nested
  class _to_string {
    @Test
    void argless() {
      assertThat(set("abc", "def").toString()).isEqualTo("[abc, def]");
    }

    @Test
    void with_delimiter() {
      assertThat(set("abc", "def").toString(":")).isEqualTo("abc:def");
    }

    @Test
    void with_prefix_delimiter_suffix() {
      assertThat(set("abc", "def").toString("{", ":", "}")).isEqualTo("{abc:def}");
    }
  }

  @Test
  void equals_and_hashcode_test() {
    new EqualsTester()
        .addEqualityGroup(set(), set())
        .addEqualityGroup(set(1), set(1), set(1, 1))
        .addEqualityGroup(set(1, 2), set(1, 2))
        .addEqualityGroup(set(2, 3), set(2, 3))
        .addEqualityGroup(set(1, 2, 3), set(1, 2, 3))
        .testEquals();
  }
}
