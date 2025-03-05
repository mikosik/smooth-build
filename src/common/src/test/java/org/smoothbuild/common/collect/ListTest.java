package org.smoothbuild.common.collect;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.collect.List.generateList;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.List.nCopiesList;
import static org.smoothbuild.common.collect.List.pullUpMaybe;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.common.tuple.Tuples.tuple;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import com.google.common.testing.EqualsTester;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.common.function.Function1;

public class ListTest {
  @Nested
  class _list {
    @Test
    void with_no_elements() {
      assertThat(list()).isEmpty();
    }

    @Test
    void with_one_element() {
      assertThat(list("abc")).containsExactly("abc").inOrder();
    }

    @Test
    void with_two_elements() {
      assertThat(list("abc", "def")).containsExactly("abc", "def").inOrder();
    }

    @Test
    void with_three_elements() {
      assertThat(list("abc", "def", "ghi")).containsExactly("abc", "def", "ghi").inOrder();
    }
  }

  @Nested
  class _generateList_function0 {
    @Test
    void with_no_elements() {
      assertThat(generateList(0, () -> 1)).isEmpty();
    }

    @Test
    void with_one_element() {
      assertThat(generateList(1, () -> 1)).isEqualTo(list(1));
    }

    @Test
    void with_many_elements() {
      var index = new AtomicInteger(0);
      assertThat(generateList(5, index::getAndIncrement)).isEqualTo(list(0, 1, 2, 3, 4));
    }

    @Test
    void exception_from_supplier_is_propagated() {
      var exception = new Exception("message");
      Function0<String, Exception> supplier = () -> {
        throw exception;
      };
      assertCall(() -> generateList(5, supplier)).throwsException(exception);
    }
  }

  @Nested
  class _generateList_function1 {
    @Test
    void with_no_elements() {
      assertThat(generateList(0, (i) -> 1)).isEmpty();
    }

    @Test
    void with_one_element() {
      assertThat(generateList(1, Object::toString)).isEqualTo(list("0"));
    }

    @Test
    void with_many_elements() {
      assertThat(generateList(5, Object::toString)).isEqualTo(list("0", "1", "2", "3", "4"));
    }

    @Test
    void exception_from_supplier_is_propagated() {
      var exception = new Exception("message");
      Function1<Integer, String, Exception> supplier = (i) -> {
        throw exception;
      };
      assertCall(() -> generateList(5, supplier)).throwsException(exception);
    }
  }

  @Nested
  class _nCopiesList {
    @Test
    void with_no_elements() {
      assertThat(nCopiesList(0, 1)).isEmpty();
    }

    @Test
    void with_one_element() {
      assertThat(nCopiesList(1, 1)).isEqualTo(list(1));
    }

    @Test
    void with_many_elements() {
      assertThat(nCopiesList(5, 1)).isEqualTo(list(1, 1, 1, 1, 1));
    }
  }

  @Nested
  class _listOfAll {
    @Nested
    class _with_collection_param {
      @Nested
      class _passing_list_argument {
        @Test
        void returns_same_instance() {
          var list = list(1, 2, 3);
          assertThat(listOfAll(list)).isSameInstanceAs(list);
        }

        @Test
        void returns_same_instance_for_empty_list() {
          var list = list();
          assertThat(listOfAll(list)).isSameInstanceAs(list);
        }
      }

      @Nested
      class _passing_set_argument {
        @Test
        void returns_list_with_same_elements() {
          var set = set(1, 2, 3);
          var list = listOfAll(set);
          assertThat(list).containsExactlyElementsIn(set);
        }

        @Test
        void returns_same_instance_for_empty_list() {
          var set = set();
          var list = listOfAll(set);
          assertThat(list).containsExactlyElementsIn(set);
        }
      }
    }

    @Nested
    class _with_jdk_collection_param {
      @Nested
      class _passing_jdk_list_argument {
        @Test
        void returns_list_with_same_elements() {
          var jdkList = java.util.List.of(1, 2, 3);
          var list = listOfAll(jdkList);
          assertThat(list).containsExactlyElementsIn(jdkList);
        }

        @Test
        void returns_empty_list_for_empty_list_argument() {
          var jdkList = java.util.List.of();
          var list = listOfAll(jdkList);
          assertThat(list).containsExactlyElementsIn(jdkList);
        }
      }

      @Nested
      class _passing_jdk_set_argument {
        @Test
        void returns_list_with_same_elements() {
          var jdkSet = java.util.Set.of(1, 2, 3);
          var list = listOfAll(jdkSet);
          assertThat(list).containsExactlyElementsIn(jdkSet);
        }

        @Test
        void returns_empty_list_for_empty_list_argument() {
          var jdkSet = java.util.Set.of();
          var list = listOfAll(jdkSet);
          assertThat(list).containsExactlyElementsIn(jdkSet);
        }
      }
    }
  }

  @Nested
  class _isEmpty {
    @Test
    void returns_true_for_empty_list() {
      var list = list();
      assertThat(list.isEmpty()).isTrue();
    }

    @Test
    void returns_false_for_non_empty_list() {
      var list = list(1);
      assertThat(list.isEmpty()).isFalse();
    }
  }

  @Nested
  class _contains {
    @Test
    void returns_true_when_list_contains_element() {
      var list = list(1, 2, 3);
      assertThat(list.contains(2)).isTrue();
    }

    @Test
    void returns_false_whne_list_not_contains_element() {
      var list = list(1, 2, 3);
      assertThat(list.contains(4)).isFalse();
    }
  }

  @Nested
  class _iterator {
    @Test
    void returns_iterator_with_all_elements() {
      var list = list(1, 2, 3);
      assertThat(newArrayList(list.iterator())).containsExactly(1, 2, 3);
    }

    @Test
    void returns_empty_iterator_for_empty_list() {
      var list = list();
      assertThat(newArrayList(list.iterator())).isEmpty();
    }

    @Test
    void returns_iterator_which_remove_method_fails_with_exception() {
      var list = list(1, 2, 3);
      assertCall(() -> list.iterator().remove())
          .throwsException(UnsupportedOperationException.class);
    }
  }

  @Nested
  class _size {
    @ParameterizedTest
    @MethodSource
    void of_empty_list_is_zero(List<?> list, int expectedSize) {
      assertThat(list.size()).isEqualTo(expectedSize);
    }

    static Stream<Arguments> of_empty_list_is_zero() {
      return Stream.of(
          arguments(list(), 0),
          arguments(list(1), 1),
          arguments(list(1, 2), 2),
          arguments(list(1, 2, 3), 3),
          arguments(list(1, 2, 3, 4), 4),
          arguments(list(1, 2, 3, 2), 4));
    }
  }

  @Nested
  class _toList {
    @Test
    void returns_this() {
      var list = list(1, 2, 3);
      assertThat(list.toList()).isSameInstanceAs(list);
    }
  }

  @Nested
  class _toSet {
    @Test
    void on_empty_list_returns_empty_set() {
      assertThat(list().toSet()).isEqualTo(set());
    }

    @Test
    void removes_duplicates() {
      assertThat(list(1, 2, 3, 2).toSet()).isEqualTo(set(1, 2, 3));
    }

    @Test
    void keeps_order_unchanged() {
      assertThat(list(3, 2, 1, 4, 7, 6).toSet())
          .containsExactly(3, 2, 1, 4, 7, 6)
          .inOrder();
    }

    @Test
    void returns_instance_of_custom_set() {
      assertThat(list(1).toSet()).isInstanceOf(Set.class);
    }
  }

  @Nested
  class _construct {
    @Test
    void with_empty_list() {
      var list = list();
      var result = list.construct(List::size);
      assertThat(result).isEqualTo(0);
    }

    @Test
    void with_non_empty_list() {
      var list = list(1, 2, 3);
      var result = list.construct(List::size);
      assertThat(result).isEqualTo(3);
    }

    @Test
    void exception_from_constructor_is_propagated() {
      var list = list(1, 2, 3);
      var exception = new Exception("message");
      Function1<List<Integer>, Integer, Exception> constructor = l -> {
        throw exception;
      };
      assertCall(() -> list.construct(constructor)).throwsException(exception);
    }
  }

  @Nested
  class _stream {
    @Test
    void provides_all_list_elements() {
      var list = list(1, 2, 3);
      assertThat(list.stream().toList()).containsExactly(1, 2, 3);
    }

    @Test
    void provides_no_elements_for_empty_list() {
      var list = list();
      assertThat(list.stream().toList()).isEmpty();
    }
  }

  @Nested
  class _asJdkList {
    @Test
    void on_empty_list_returns_empty_jdk_list() {
      var jdkList = list().asJdkList();
      assertThat(jdkList).isInstanceOf(java.util.List.class);
      assertThat(jdkList).isEqualTo(asList());
    }

    @Test
    void returns_jdk_list_with_same_elements() {
      var jdkList = list(1, 2, 3).asJdkList();
      assertThat(jdkList).isInstanceOf(java.util.List.class);
      assertThat(jdkList).isEqualTo(asList(1, 2, 3));
    }

    @Test
    void jdk_list_size_is_correct() {
      var jdkList = list(1, 2, 3).asJdkList();
      assertThat(jdkList.size()).isEqualTo(3);
    }

    @Test
    void jdk_list_get_returns_correct_element() {
      var jdkList = list(1, 2, 3).asJdkList();
      assertThat(jdkList.get(1)).isEqualTo(2);
    }

    @Test
    void jdk_list_get_throws_exception_for_invalid_index() {
      var jdkList = list(1, 2, 3).asJdkList();
      assertCall(() -> jdkList.get(3))
          .throwsException(new NoSuchElementException("index = 3, list.size() = 3"));
    }

    @Test
    void jdk_list_is_immutable() {
      var jdkList = list(1, 2, 3).asJdkList();
      assertCall(() -> jdkList.add(1)).throwsException(UnsupportedOperationException.class);
      assertCall(() -> jdkList.set(0, 2)).throwsException(UnsupportedOperationException.class);
      assertCall(() -> jdkList.remove(1)).throwsException(UnsupportedOperationException.class);
      assertCall(() -> jdkList.clear()).throwsException(UnsupportedOperationException.class);
      assertCall(() -> jdkList.addAll(asList(4, 5)))
          .throwsException(UnsupportedOperationException.class);
      assertCall(() -> jdkList.removeAll(asList(1, 2)))
          .throwsException(UnsupportedOperationException.class);
      assertCall(() -> jdkList.retainAll(asList(1, 2)))
          .throwsException(UnsupportedOperationException.class);
    }
  }

  @Nested
  class _immutability {
    @Test
    void list_created_from_varargs_makes_defensive_copy_of_an_array() {
      var array = new Integer[] {1, 2, 3};
      var list = list(array);

      array[0] = 7;

      assertThat(list).containsExactly(1, 2, 3).inOrder();
    }

    @Test
    void as_copy_of_other_list_makes_defensive_copy() {
      var original = asList(1, 2, 3);

      var copy = listOfAll(original);
      original.set(0, 7);

      assertThat(copy).containsExactly(1, 2, 3).inOrder();
    }

    @Test
    void as_copy_of_other_list_creates_different_instance() {
      var list = asList(1, 2, 3);
      assertThat(listOfAll(list)).isNotSameInstanceAs(list);
    }
  }

  @Nested
  class _sublist {
    @Test
    void returns_sublist() {
      var list = list(1, 2, 3);
      assertThat(list.subList(1, 2)).isEqualTo(list(2));
    }

    @Test
    void is_covariant() {
      var list = list(1, 2, 3);
      assertThat(list.subList(1, 2)).isInstanceOf(List.class);
    }
  }

  @Nested
  class _reverse {
    @Test
    void empty_lists_returns_empty_list() {
      assertThat(list().reverse()).isEqualTo(list());
    }

    @Test
    void list_with_one_element() {
      assertThat(list(1).reverse()).isEqualTo(list(1));
    }

    @Test
    void list_with_many_elements() {
      assertThat(list(1, 2, 3, 4).reverse()).isEqualTo(list(4, 3, 2, 1));
    }
  }

  @Nested
  class _rotate {
    @Test
    void empty_lists_returns_empty_list() {
      assertThat(list().rotate(3)).isEqualTo(list());
    }

    @ParameterizedTest
    @ValueSource(ints = {-9, -6, -3, 0, 3, 6, 9})
    void rotated_by_zero_or_full_cycle_returns_same_instance(int distance) {
      var list = list(1, 2, 3);
      assertThat(list.rotate(distance)).isSameInstanceAs(list);
    }

    @ParameterizedTest
    @ValueSource(ints = {-7, -4, -1, 2, 5, 8, 11})
    void rotated_not_full_cycle(int distance) {
      var list = list(1, 2, 3);
      assertThat(list.rotate(distance)).isEqualTo(list(2, 3, 1));
    }
  }

  @Nested
  class _addAll {
    @Nested
    class _jdk_collection {
      @Test
      void two_empty_lists() {
        assertThat(list().addAll(asList())).isEqualTo(list());
      }

      @Test
      void empty_list() {
        assertThat(list(1).addAll(asList())).isEqualTo(list(1));
      }

      @Test
      void to_empty_list() {
        assertThat(list().addAll(asList(2))).isEqualTo(list(2));
      }

      @Test
      void two_one_element_lists() {
        assertThat(list(1).addAll(asList(2))).isEqualTo(list(1, 2));
      }

      @Test
      void element_lists() {
        assertThat(list(1, 2, 3).addAll(asList(4, 5, 6))).isEqualTo(list(1, 2, 3, 4, 5, 6));
      }
    }

    @Nested
    class _custom_list {
      @Test
      void two_empty_lists() {
        assertThat(list().addAll(list())).isEqualTo(list());
      }

      @Test
      void empty_list() {
        assertThat(list(1).addAll(list())).isEqualTo(list(1));
      }

      @Test
      void to_empty_list() {
        assertThat(list().addAll(list(2))).isEqualTo(list(2));
      }

      @Test
      void two_one_element_lists() {
        assertThat(list(1).addAll(list(2))).isEqualTo(list(1, 2));
      }

      @Test
      void element_lists() {
        assertThat(list(1, 2, 3).addAll(list(4, 5, 6))).isEqualTo(list(1, 2, 3, 4, 5, 6));
      }
    }

    @Nested
    class _custom_set {
      @Test
      void both_empty() {
        assertThat(list().addAll(set())).isEqualTo(list());
      }

      @Test
      void empty_set() {
        assertThat(list(1).addAll(set())).isEqualTo(list(1));
      }

      @Test
      void to_empty_list() {
        assertThat(list().addAll(set(2))).isEqualTo(list(2));
      }

      @Test
      void two_one_element_lists() {
        assertThat(list(1).addAll(set(2))).isEqualTo(list(1, 2));
      }

      @Test
      void with_many_elements() {
        assertThat(list(1, 2, 3).addAll(set(4, 5, 6))).isEqualTo(list(1, 2, 3, 4, 5, 6));
      }
    }
  }

  @Nested
  class _add {
    @Test
    void nothing_to_empty_lists() {
      assertThat(list().add()).isEqualTo(list());
    }

    @Test
    void nothing_to_list() {
      assertThat(list(1).add()).isEqualTo(list(1));
    }

    @Test
    void one_element_to_empty_list() {
      assertThat(list().add(2)).isEqualTo(list(2));
    }

    @Test
    void one_element_to_one_element_list() {
      assertThat(list(1).add(2)).isEqualTo(list(1, 2));
    }

    @Test
    void many_elements_to_many_element_list() {
      assertThat(list(1, 2, 3).add(4, 5, 6)).isEqualTo(list(1, 2, 3, 4, 5, 6));
    }
  }

  @Nested
  class _collection_methods extends AbstractCollectionTestSuite {
    @SafeVarargs
    @Override
    public final <E> Collection<E> newCollection(E... elements) {
      return list(elements);
    }
  }

  @Nested
  class _get {
    @Test
    void returns_element_at_given_index() {
      assertThat(list(1, 2, 3, 4).get(2)).isEqualTo(3);
    }

    @Test
    void throws_exception_for_negative_index() {
      assertCall(() -> list(1, 2, 3, 4).get(-1)).throwsException(NoSuchElementException.class);
    }

    @Test
    void throws_exception_for_index_equal_to_list_size() {
      assertCall(() -> list(1, 2, 3, 4).get(4)).throwsException(NoSuchElementException.class);
    }
  }

  @Nested
  class _last {
    @Test
    void returns_last_element() {
      assertThat(list(1, 2, 3, 4).last()).isEqualTo(4);
    }

    @Test
    void throws_exception_for_empty_list() {
      assertCall(() -> list().last()).throwsException(NoSuchElementException.class);
    }
  }

  @Nested
  class _indexOf {
    @Test
    void returns_index_of_given_element() {
      assertThat(list(1, 2, 3, 4).indexOf(3)).isEqualTo(2);
    }

    @Test
    void returns_negative_one_when_element_not_exists() {
      assertThat(list(1, 2, 3, 4).indexOf(5)).isEqualTo(-1);
    }
  }

  @Nested
  class _sort_using {
    @Test
    void empty_list() {
      assertThat(list().sortUsing(comparing(Object::toString))).isEqualTo(list());
    }

    @Test
    void single_element_list() {
      assertThat(list(1).sortUsing(comparing(Object::toString))).isEqualTo(list(1));
    }

    @Test
    void two_elements_list() {
      assertThat(list(2, 1).sortUsing(comparing(Object::toString))).isEqualTo(list(1, 2));
    }

    @Test
    void many_elements_list() {
      assertThat(list(2, 1, 5, 6, 3, 4, 7).sortUsing(comparing(Object::toString)))
          .isEqualTo(list(1, 2, 3, 4, 5, 6, 7));
    }
  }

  @Nested
  class _map {
    @Test
    void returns_empty_list_for_empty_arg() {
      assertThat(List.<String>list().map(String::toUpperCase)).isEmpty();
    }

    @Test
    void returns_mapped_one_element() {
      assertThat(list("abc").map(String::toUpperCase)).containsExactly("ABC").inOrder();
    }

    @Test
    void mapping_with_two_element() {
      assertThat(list("abc", "def").map(String::toUpperCase))
          .containsExactly("ABC", "DEF")
          .inOrder();
    }

    @Test
    void exception_from_mapper_is_propagated() {
      assertCall(() -> list("abc").map(this::throwRuntimeException))
          .throwsException(RuntimeException.class);
    }

    private Object throwRuntimeException(String string) {
      throw new RuntimeException();
    }
  }

  @Nested
  class _flatMap {
    @Test
    void returns_empty_list_for_empty_arg() {
      assertThat(List.<String>list().flatMap(s -> list(s, s))).isEmpty();
    }

    @Test
    void returns_doubled_element() {
      assertThat(list("abc").flatMap(s -> list(s, s)))
          .containsExactly("abc", "abc")
          .inOrder();
    }

    @Test
    void returns_doubled_elements() {
      assertThat(list("abc", "def").flatMap(s -> list(s, s)))
          .containsExactly("abc", "abc", "def", "def")
          .inOrder();
    }

    @Test
    void exception_from_mapper_is_propagated() {
      assertCall(() -> list("abc").flatMap(this::throwRuntimeException))
          .throwsException(RuntimeException.class);
    }

    private Iterable<Object> throwRuntimeException(String string) {
      throw new RuntimeException();
    }
  }

  @Nested
  class _filter {
    @Test
    void returns_empty_for_empty_list() {
      assertThat(list().filter(x -> true)).isEmpty();
    }

    @Test
    void returns_unmodified_list_when_predicate_is_always_true() {
      assertThat(list("first", "second", "third").filter(x -> true))
          .containsExactly("first", "second", "third")
          .inOrder();
    }

    @Test
    void returns_empty_list_when_predicate_is_always_false() {
      assertThat(list("first", "second", "third").filter(x -> false)).isEmpty();
    }

    @Test
    void leaves_only_elements_matching_predicate() {
      assertThat(list("first", "second", "third").filter(s -> s.startsWith("s")))
          .containsExactly("second")
          .inOrder();
    }

    @Test
    void propagates_exception_thrown_from_function() {
      var exception = new Exception("message");
      var list = list("first", "second", "third");
      assertCall(() -> list.filter(s -> {
            throw exception;
          }))
          .throwsException(exception);
    }
  }

  @Nested
  class _drop_while {
    @Test
    void for_empty_list_returns_empty_list() {
      assertThat(list().dropWhile(s -> true)).isEmpty();
    }

    @Test
    void for_list_with_all_elements_matching_returns_empty_list() {
      var list = list(1, 2, 3);
      assertThat(list.dropWhile(s -> true)).isEqualTo(list());
    }

    @Test
    void for_list_with_none_elements_matching_returns_copy_of_that_list() {
      var list = list(1, 2, 3);
      assertThat(list.dropWhile(s -> false)).isEqualTo(list);
    }

    @Test
    void removes_leading_matches() {
      var list = list(1, 1, 2, 3);
      assertThat(list.dropWhile(s -> s.equals(1))).isEqualTo(list(2, 3));
    }

    @Test
    void not_removes_non_leading_matches() {
      var list = list(1, 2, 3);
      assertThat(list.dropWhile(s -> s.equals(2))).isEqualTo(list);
    }

    @Test
    void exception_from_predicate_is_propagated() {
      var exception = new Exception("message");
      assertCall(() -> list(1).dropWhile(e -> {
            throw exception;
          }))
          .throwsException(exception);
    }
  }

  @Nested
  class _take_while {
    @Test
    void for_empty_list_returns_empty_list() {
      assertThat(list().takeWhile(s -> true)).isEmpty();
    }

    @Test
    void for_list_with_all_elements_matching_returns_copy_of_that_list() {
      var list = list(1, 2, 3);
      assertThat(list.takeWhile(s -> true)).isEqualTo(list(1, 2, 3));
    }

    @Test
    void for_list_with_none_elements_matching_returns_empty_list() {
      var list = list(1, 2, 3);
      assertThat(list.takeWhile(s -> false)).isEqualTo(list());
    }

    @Test
    void takes_leading_matches() {
      var list = list(1, 1, 2, 3);
      assertThat(list.takeWhile(s -> s.equals(1))).isEqualTo(list(1, 1));
    }

    @Test
    void not_takes_non_leading_matches() {
      var list = list(1, 2, 3);
      assertThat(list.takeWhile(s -> s.equals(1))).isEqualTo(list(1));
    }

    @Test
    void exception_from_predicate_is_propagated() {
      var exception = new Exception("message");
      assertCall(() -> list(1).takeWhile(e -> {
            throw exception;
          }))
          .throwsException(exception);
    }
  }

  @Nested
  class _zip {
    @Test
    void with_empty_iterable_fails() {
      var list1 = list(1);
      var list2 = list();
      assertCall(() -> list1.zip(list2, (x, y) -> 7))
          .throwsException(new IllegalArgumentException(
              "Cannot zip with Iterable of different size: expected 1, got 0"));
    }

    @Test
    void empty_with_non_empty_iterable_fails() {
      var list1 = list();
      var list2 = list(1);
      assertCall(() -> list1.zip(list2, (x, y) -> 7))
          .throwsException(new IllegalArgumentException(
              "Cannot zip with Iterable of different size: expected 0, got 1"));
    }

    @Test
    void with_iterable_of_equal_size() {
      assertThat(list(1, 2).zip(list(10, 20), Integer::sum))
          .containsExactly(11, 22)
          .inOrder();
    }

    @Test
    void throwable_thrown_from_function_is_propagated() {
      assertCall(() -> list(1, 2).zip(list(1, 2), (x, y) -> {
            throw new Exception();
          }))
          .throwsException(Exception.class);
    }

    @Test
    void with_shorter_iterable_fails() {
      var list1 = list(1, 2);
      var list2 = list(3);
      assertCall(() -> list1.zip(list2, Integer::sum))
          .throwsException(new IllegalArgumentException(
              "Cannot zip with Iterable of different size: expected 2, got 1"));
    }

    @Test
    void with_longer_iterable_fails() {
      var list1 = list(3);
      var list2 = list(1, 2);
      assertCall(() -> list1.zip(list2, (x, y) -> x + y))
          .throwsException(new IllegalArgumentException(
              "Cannot zip with Iterable of different size: expected 1, got 2"));
    }
  }

  @Nested
  class _zipWithIndex {
    @Test
    void empty_list() {
      assertThat(list().zipWithIndex()).isEqualTo(list());
    }

    @Test
    void one_element_list() {
      assertThat(list(1).zipWithIndex()).isEqualTo(list(tuple(1, 0)));
    }

    @Test
    void two_elements_list() {
      assertThat(list(1, 2).zipWithIndex()).isEqualTo(list(tuple(1, 0), tuple(2, 1)));
    }
  }

  @ParameterizedTest
  @MethodSource
  void startsWith(List<String> label, List<String> prefix, boolean expected) {
    assertThat(label.startsWith(prefix)).isEqualTo(expected);
  }

  public static java.util.List<Arguments> startsWith() {
    return java.util.List.of(
        arguments(list(), list(), true),
        arguments(list(), list(1), false),
        arguments(list(1), list(), true),
        arguments(list(1), list(1), true),
        arguments(list(1), list(2), false),
        arguments(list(1), list(1, 2), false),
        arguments(list(1, 2), list(), true),
        arguments(list(1, 2), list(1), true),
        arguments(list(1, 2), list(2), false),
        arguments(list(1, 2), list(1, 2), true),
        arguments(list(1, 2), list(1, 3), false),
        arguments(list(1, 2), list(1, 2, 3), false));
  }

  @Nested
  class _toMap_with_value_mapper {
    @Test
    void on_empty_list_returns_empty_map() {
      assertThat(list().toMap(Object::toString)).isEqualTo(map());
    }

    @Test
    void returns_map_with_calculated_values() {
      assertThat(list(1, 2).toMap(x -> -x)).isEqualTo(map(1, -1, 2, -2));
    }

    @Test
    void propagates_exception_from_value_mapper() {
      var exception = new Exception("message");
      assertCall(() -> list(1).toMap(e -> {
            throw exception;
          }))
          .throwsException(exception);
    }

    @Test
    void returns_map_with_order_unchanged() {
      var list = list(3, 2, 1, 4, 7, 6);
      var map = list.toMap(x -> -x);
      assertThat(map.keySet()).containsExactly(3, 2, 1, 4, 7, 6).inOrder();
      assertThat(map.values()).containsExactly(-3, -2, -1, -4, -7, -6).inOrder();
    }
  }

  @Nested
  class _toMap_with_key_and_value_mapper {
    @Test
    void on_empty_list_returns_empty_map() {
      List<String> list = list();
      assertThat(list.toMap(e -> e + e, String::toUpperCase)).isEqualTo(map());
    }

    @Test
    void returns_map_with_calculated_values() {
      assertThat(list(1, 2).toMap(e -> -e, Object::toString)).isEqualTo(map(-1, "1", -2, "2"));
    }

    @Test
    void propagates_exception_from_value_mapper() {
      var exception = new Exception("message");
      Function1<Integer, Object, Exception> thrower = e -> {
        throw exception;
      };
      assertCall(() -> list(1).toMap(thrower, Object::toString)).throwsException(exception);
    }

    @Test
    void propagates_exception_from_key_mapper() {
      var exception = new Exception("message");
      Function1<Integer, Object, Exception> thrower = e -> {
        throw exception;
      };
      assertCall(() -> list(1).toMap(Object::toString, thrower)).throwsException(exception);
    }

    @Test
    void returns_map_with_order_unchanged() {
      var list = list(3, 2, 1, 4, 7, 6);
      var map = list.toMap(e -> -e, Object::toString);
      assertThat(map.keySet()).containsExactly(-3, -2, -1, -4, -7, -6).inOrder();
      assertThat(map.values()).containsExactly("3", "2", "1", "4", "7", "6").inOrder();
    }
  }

  @Nested
  class _pull_up_maybe {
    @Test
    void with_zero_elements() {
      assertThat(pullUpMaybe(list())).isEqualTo(some(list()));
    }

    @Test
    void with_none() {
      assertThat(pullUpMaybe(list(some("abc"), none()))).isEqualTo(none());
    }

    @Test
    void with_all_elements_present() {
      assertThat(pullUpMaybe(list(some("abc"), some("def")))).isEqualTo(some(list("abc", "def")));
    }
  }

  @Nested
  class _to_string {
    @Test
    void argless() {
      assertThat(list("abc", "def").toString()).isEqualTo("[abc, def]");
    }

    @Test
    void with_delimiter() {
      assertThat(list("abc", "def").toString(":")).isEqualTo("abc:def");
    }

    @Test
    void with_prefix_delimiter_suffix() {
      assertThat(list("abc", "def").toString("{", ":", "}")).isEqualTo("{abc:def}");
    }
  }

  @Test
  void equals_and_hashcode_test() {
    new EqualsTester()
        .addEqualityGroup(list(), list())
        .addEqualityGroup(list(1), list(1))
        .addEqualityGroup(list("x"), List.<Object>list("x"))
        .addEqualityGroup(list(1, 2), list(1, 2))
        .addEqualityGroup(list(1, 2, 3), list(1, 2, 3))
        .testEquals();
  }
}
