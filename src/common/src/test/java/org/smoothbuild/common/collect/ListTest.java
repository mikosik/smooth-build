package org.smoothbuild.common.collect;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.common.collect.List.generateList;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.List.nCopiesList;
import static org.smoothbuild.common.collect.List.pullUpMaybe;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.tuple.Tuples.tuple;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import com.google.common.testing.EqualsTester;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.common.function.Consumer1;
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
      assertThat(generateList(0, () -> "a")).isEmpty();
    }

    @Test
    void with_one_element() {
      assertThat(generateList(1, () -> "a")).isEqualTo(list("a"));
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
      assertThat(generateList(0, (i) -> "a")).isEmpty();
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
      assertThat(nCopiesList(0, "a")).isEmpty();
    }

    @Test
    void with_one_element() {
      assertThat(nCopiesList(1, "a")).isEqualTo(list("a"));
    }

    @Test
    void with_many_elements() {
      assertThat(nCopiesList(5, "a")).isEqualTo(list("a", "a", "a", "a", "a"));
    }
  }

  @Nested
  class _listOfAll {
    @Test
    void as_copy_of_other_list() {
      var list = asList("a", "b", "c");
      var copy = listOfAll(list);
      assertThat(copy).isEqualTo(list);
      assertThat(copy).isNotSameInstanceAs(list);
    }

    @Test
    void as_copy_returns_argument_when_it_is_instance_of_this_class() {
      var list = list("a", "b", "c");
      assertThat(listOfAll(list)).isSameInstanceAs(list);
    }
  }

  @Nested
  class _immutability {
    @Test
    void list_created_from_varargs_makes_defensive_copy_of_an_array() {
      var array = new String[] {"a", "b", "c"};
      var list = list(array);

      array[0] = "x";

      assertThat(list).containsExactly("a", "b", "c").inOrder();
    }

    @Test
    void as_copy_of_other_list_makes_defensive_copy() {
      var original = asList("a", "b", "c");

      var copy = listOfAll(original);
      original.set(0, "x");

      assertThat(copy).containsExactly("a", "b", "c").inOrder();
    }

    @Test
    void as_copy_of_other_list_creates_different_instance() {
      var list = asList("a", "b", "c");
      assertThat(listOfAll(list)).isNotSameInstanceAs(list);
    }

    @Test
    void iterator_remove_fails() {
      var list = list("a", "b", "c");
      var iterator = list.iterator();
      iterator.next();
      assertCall(iterator::remove).throwsException(UnsupportedOperationException.class);
    }

    @Test
    void set_fails() {
      var list = list("a", "b", "c");
      assertCall(() -> list.set(0, "x")).throwsException(UnsupportedOperationException.class);
    }

    @Test
    void add_fails() {
      var list = list("a", "b", "c");
      assertCall(() -> list.add("x")).throwsException(UnsupportedOperationException.class);
    }

    @Test
    void add_with_index_fails() {
      var list = list("a", "b", "c");
      assertCall(() -> list.add(0, "x")).throwsException(UnsupportedOperationException.class);
    }

    @Test
    void addAll_fails() {
      var list = list("a", "b", "c");
      assertCall(() -> list.addAll(asList("d", "e")))
          .throwsException(UnsupportedOperationException.class);
    }

    @Test
    void addAll_with_index_fails() {
      var list = list("a", "b", "c");
      assertCall(() -> list.addAll(2, asList("d", "e")))
          .throwsException(UnsupportedOperationException.class);
    }

    @Test
    void clear_fails() {
      var list = list("a", "b", "c");
      assertCall(list::clear).throwsException(UnsupportedOperationException.class);
    }

    @Test
    void sort_fails() {
      var list = list("a", "b", "c");
      assertCall(() -> list.sort(Comparator.naturalOrder()))
          .throwsException(UnsupportedOperationException.class);
    }

    @Test
    void addFirst_fails() {
      var list = list("a", "b", "c");
      assertCall(() -> list.addFirst("x")).throwsException(UnsupportedOperationException.class);
    }

    @Test
    void addLast_fails() {
      var list = list("a", "b", "c");
      assertCall(() -> list.addLast("x")).throwsException(UnsupportedOperationException.class);
    }

    @Test
    void remove_fails() {
      var list = list("a", "b", "c");
      assertCall(() -> list.remove("a")).throwsException(UnsupportedOperationException.class);
    }

    @Test
    void remove_with_index_fails() {
      var list = list("a", "b", "c");
      assertCall(() -> list.remove(0)).throwsException(UnsupportedOperationException.class);
    }

    @Test
    void removeFirst_fails() {
      var list = list("a", "b", "c");
      assertCall(list::removeFirst).throwsException(UnsupportedOperationException.class);
    }

    @Test
    void removeLast_fails() {
      var list = list("a", "b", "c");
      assertCall(list::removeFirst).throwsException(UnsupportedOperationException.class);
    }

    @Test
    void removeIf_fails() {
      var list = list("a", "b", "c");
      assertCall(() -> list.removeIf(e -> true))
          .throwsException(UnsupportedOperationException.class);
    }
  }

  @Nested
  class _sublist {
    @Test
    void returns_sublist() {
      var list = list("a", "b", "c");
      assertThat(list.subList(1, 2)).isEqualTo(list("b"));
    }

    @Test
    void is_covariant() {
      var list = list("a", "b", "c");
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
      assertThat(list("a").reverse()).isEqualTo(list("a"));
    }

    @Test
    void list_with_many_elements() {
      assertThat(list("a", "b", "c", "d").reverse()).isEqualTo(list("d", "c", "b", "a"));
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
      var list = list("a", "b", "c");
      assertThat(list.rotate(distance)).isSameInstanceAs(list);
    }

    @ParameterizedTest
    @ValueSource(ints = {-7, -4, -1, 2, 5, 8, 11})
    void rotated_not_full_cycle(int distance) {
      var list = list("a", "b", "c");
      assertThat(list.rotate(distance)).isEqualTo(list("b", "c", "a"));
    }
  }

  @Nested
  class _appendAll {
    @Test
    void two_empty_lists() {
      assertThat(list().appendAll(asList())).isEqualTo(list());
    }

    @Test
    void empty_list() {
      assertThat(list("a").appendAll(asList())).isEqualTo(list("a"));
    }

    @Test
    void to_empty_list() {
      assertThat(list().appendAll(asList("b"))).isEqualTo(list("b"));
    }

    @Test
    void two_one_element_lists() {
      assertThat(list("a").appendAll(asList("b"))).isEqualTo(list("a", "b"));
    }

    @Test
    void element_lists() {
      assertThat(list("a", "b", "c").appendAll(asList("d", "e", "f")))
          .isEqualTo(list("a", "b", "c", "d", "e", "f"));
    }
  }

  @Nested
  class _append {
    @Test
    void nothing_to_empty_lists() {
      assertThat(list().append()).isEqualTo(list());
    }

    @Test
    void nothing_to_list() {
      assertThat(list("a").append()).isEqualTo(list("a"));
    }

    @Test
    void one_element_to_empty_list() {
      assertThat(list().append("b")).isEqualTo(list("b"));
    }

    @Test
    void one_element_to_one_element_list() {
      assertThat(list("a").append("b")).isEqualTo(list("a", "b"));
    }

    @Test
    void many_elements_to_many_element_list() {
      assertThat(list("a", "b", "c").append("d", "e", "f"))
          .isEqualTo(list("a", "b", "c", "d", "e", "f"));
    }
  }

  @Nested
  class _forEach {
    @Test
    void empty_list() throws Exception {
      Consumer1<Object, Exception> consumer1 = mock();
      list().withEach(consumer1::accept);
      verifyNoInteractions(consumer1);
    }

    @Test
    void consumes_elements_in_order() {
      var collected = new ArrayList<Integer>();
      list(1, 2, 3, 4).forEach(collected::add);
      assertThat(collected).containsExactly(1, 2, 3, 4).inOrder();
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
      assertThat(list("a").sortUsing(comparing(Object::toString))).isEqualTo(list("a"));
    }

    @Test
    void two_elements_list() {
      assertThat(list("b", "a").sortUsing(comparing(Object::toString))).isEqualTo(list("a", "b"));
    }

    @Test
    void many_elements_list() {
      assertThat(list("b", "a", "e", "f", "c", "d", "g").sortUsing(comparing(Object::toString)))
          .isEqualTo(list("a", "b", "c", "d", "e", "f", "g"));
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
      var list = list("a", "b", "c");
      assertThat(list.dropWhile(s -> true)).isEqualTo(list());
    }

    @Test
    void for_list_with_none_elements_matching_returns_copy_of_that_list() {
      var list = list("a", "b", "c");
      assertThat(list.dropWhile(s -> false)).isEqualTo(list);
    }

    @Test
    void removes_leading_matches() {
      var list = list("a", "a", "b", "c");
      assertThat(list.dropWhile(s -> s.equals("a"))).isEqualTo(list("b", "c"));
    }

    @Test
    void not_removes_non_leading_matches() {
      var list = list("a", "b", "c");
      assertThat(list.dropWhile(s -> s.equals("b"))).isEqualTo(list);
    }

    @Test
    void exception_from_predicate_is_propagated() {
      var exception = new Exception("message");
      assertCall(() -> list("a").dropWhile(e -> {
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
      var list = list("a", "b", "c");
      assertThat(list.takeWhile(s -> true)).isEqualTo(list("a", "b", "c"));
    }

    @Test
    void for_list_with_none_elements_matching_returns_empty_list() {
      var list = list("a", "b", "c");
      assertThat(list.takeWhile(s -> false)).isEqualTo(list());
    }

    @Test
    void takes_leading_matches() {
      var list = list("a", "a", "b", "c");
      assertThat(list.takeWhile(s -> s.equals("a"))).isEqualTo(list("a", "a"));
    }

    @Test
    void not_takes_non_leading_matches() {
      var list = list("a", "b", "c");
      assertThat(list.takeWhile(s -> s.equals("a"))).isEqualTo(list("a"));
    }

    @Test
    void exception_from_predicate_is_propagated() {
      var exception = new Exception("message");
      assertCall(() -> list("a").takeWhile(e -> {
            throw exception;
          }))
          .throwsException(exception);
    }
  }

  @Nested
  class _zip {
    @Test
    void with_empty_iterable_fails() {
      var list1 = list("a");
      var list2 = list();
      assertCall(() -> list1.zip(list2, (x, y) -> x + y))
          .throwsException(new IllegalArgumentException(
              "Cannot zip with Iterable of different size: expected 1, got 0"));
    }

    @Test
    void empty_with_non_empty_iterable_fails() {
      var list1 = list();
      var list2 = list("a");
      assertCall(() -> list1.zip(list2, (x, y) -> x + y))
          .throwsException(new IllegalArgumentException(
              "Cannot zip with Iterable of different size: expected 0, got 1"));
    }

    @Test
    void with_iterable_of_equal_size() {
      assertThat(list("a", "b").zip(list("1", "2"), (x, y) -> x + y))
          .containsExactly("a1", "b2")
          .inOrder();
    }

    @Test
    void throwable_thrown_from_function_is_propagated() {
      assertCall(() -> list("a", "b").zip(list("1", "2"), (x, y) -> {
            throw new Exception();
          }))
          .throwsException(Exception.class);
    }

    @Test
    void with_shorter_iterable_fails() {
      var list1 = list("a", "b");
      var list2 = list("c");
      assertCall(() -> list1.zip(list2, (x, y) -> x + y))
          .throwsException(new IllegalArgumentException(
              "Cannot zip with Iterable of different size: expected 2, got 1"));
    }

    @Test
    void with_longer_iterable_fails() {
      var list1 = list("c");
      var list2 = list("a", "b");
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
      assertThat(list("a").zipWithIndex()).isEqualTo(list(tuple("a", 0)));
    }

    @Test
    void two_elements_list() {
      assertThat(list("a", "b").zipWithIndex()).isEqualTo(list(tuple("a", 0), tuple("b", 1)));
    }
  }

  @Nested
  class _any_matches {
    @Test
    void returns_true_when_one_matches() {
      assertThat(list("a", "b").anyMatches(x -> x.equals("b"))).isTrue();
    }

    @Test
    void returns_true_when_all_matches() {
      assertThat(list("b", "b").anyMatches(x -> x.equals("b"))).isTrue();
    }

    @Test
    void returns_false_when_none_matches() {
      assertThat(list("b", "b").anyMatches(x -> x.equals("c"))).isFalse();
    }

    @Test
    void returns_false_for_empty_list_even_when_predicate_returns_always_true() {
      assertThat(list().anyMatches(x -> true)).isFalse();
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
        arguments(list(), list("a"), false),
        arguments(list("a"), list(), true),
        arguments(list("a"), list("a"), true),
        arguments(list("a"), list("b"), false),
        arguments(list("a"), list("a", "b"), false),
        arguments(list("a", "b"), list(), true),
        arguments(list("a", "b"), list("a"), true),
        arguments(list("a", "b"), list("b"), false),
        arguments(list("a", "b"), list("a", "b"), true),
        arguments(list("a", "b"), list("a", "c"), false),
        arguments(list("a", "b"), list("a", "b", "c"), false));
  }

  @Nested
  class _toSet {
    @Test
    void on_empty_list_returns_empty_set() {
      assertThat(list().toSet()).isEqualTo(java.util.Set.of());
    }

    @Test
    void removes_duplicates() {
      assertThat(list("a", "b", "c", "b").toSet()).isEqualTo(java.util.Set.of("a", "b", "c"));
    }

    @Test
    void keeps_order_unchanged() {
      assertThat(list("c", "b", "a", "d", "g", "f").toSet())
          .containsExactly("c", "b", "a", "d", "g", "f")
          .inOrder();
    }

    @Test
    void returns_instance_of_custom_set() {
      assertThat(list("a").toSet()).isInstanceOf(Set.class);
    }
  }

  @Nested
  class _toMap_with_value_mapper {
    @Test
    void on_empty_list_returns_empty_map() {
      assertThat(list().toMap(Object::toString)).isEqualTo(java.util.Map.of());
    }

    @Test
    void returns_map_with_calculated_values() {
      assertThat(list("a", "b").toMap(String::toUpperCase))
          .isEqualTo(java.util.Map.of("a", "A", "b", "B"));
    }

    @Test
    void propagates_exception_from_value_mapper() {
      var exception = new Exception("message");
      assertCall(() -> list("a").toMap(e -> {
            throw exception;
          }))
          .throwsException(exception);
    }

    @Test
    void returns_map_with_order_unchanged() {
      var list = list("c", "b", "a", "d", "g", "f");
      var map = list.toMap(String::toUpperCase);
      assertThat(map.keySet()).containsExactly("c", "b", "a", "d", "g", "f").inOrder();
      assertThat(map.values()).containsExactly("C", "B", "A", "D", "G", "F").inOrder();
    }
  }

  @Nested
  class _toMap_with_key_and_value_mapper {
    @Test
    void on_empty_list_returns_empty_map() {
      List<String> list = list();
      assertThat(list.toMap(e -> e + e, String::toUpperCase)).isEqualTo(java.util.Map.of());
    }

    @Test
    void returns_map_with_calculated_values() {
      assertThat(list("a", "b").toMap(e -> e + e, String::toUpperCase))
          .isEqualTo(java.util.Map.of("aa", "A", "bb", "B"));
    }

    @Test
    void propagates_exception_from_value_mapper() {
      var exception = new Exception("message");
      assertCall(() -> list("a")
              .toMap(
                  e -> {
                    throw exception;
                  },
                  String::toUpperCase))
          .throwsException(exception);
    }

    @Test
    void propagates_exception_from_key_mapper() {
      var exception = new Exception("message");
      assertCall(() -> list("a").toMap(String::toUpperCase, e -> {
            throw exception;
          }))
          .throwsException(exception);
    }

    @Test
    void returns_map_with_order_unchanged() {
      var list = list("c", "b", "a", "d", "g", "f");
      var map = list.toMap(e -> e + e, String::toUpperCase);
      assertThat(map.keySet())
          .containsExactly("cc", "bb", "aa", "dd", "gg", "ff")
          .inOrder();
      assertThat(map.values()).containsExactly("C", "B", "A", "D", "G", "F").inOrder();
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
        .addEqualityGroup(list("a"), list("a"))
        .addEqualityGroup(list("x"), List.<Object>list("x"))
        .addEqualityGroup(list("a", "b"), list("a", "b"))
        .addEqualityGroup(list("a", "b", "c"), list("a", "b", "c"))
        .testEquals();
  }
}
