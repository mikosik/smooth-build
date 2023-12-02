package org.smoothbuild.common.collect;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.List.pullUpMaybe;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import com.google.common.testing.EqualsTester;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.common.function.ThrowingSupplier;

public class ListTest {
  @Nested
  class _list {
    @Test
    public void with_no_elements() {
      assertThat(list()).isEmpty();
    }

    @Test
    public void with_one_element() {
      assertThat(list("abc")).containsExactly("abc").inOrder();
    }

    @Test
    public void with_two_elements() {
      assertThat(list("abc", "def")).containsExactly("abc", "def").inOrder();
    }

    @Test
    public void with_three_elements() {
      assertThat(list("abc", "def", "ghi")).containsExactly("abc", "def", "ghi").inOrder();
    }
  }

  @Nested
  class _list_with_supplier {
    @Test
    public void with_no_elements() {
      assertThat(list(0, () -> "a")).isEmpty();
    }

    @Test
    public void with_one_element() {
      assertThat(list(1, () -> "a")).isEqualTo(list("a"));
    }

    @Test
    public void with_many_elements() {
      var index = new AtomicInteger(0);
      assertThat(list(5, index::getAndIncrement)).isEqualTo(list(0, 1, 2, 3, 4));
    }

    @Test
    public void exception_from_supplier_is_propagated() {
      var exception = new Exception("message");
      ThrowingSupplier<String, Exception> supplier = () -> {
        throw exception;
      };
      assertCall(() -> list(5, supplier)).throwsException(exception);
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
  class _sort_using {
    @Test
    public void empty_list() {
      assertThat(list().sortUsing(comparing(Object::toString))).isEqualTo(list());
    }

    @Test
    public void single_element_list() {
      assertThat(list("a").sortUsing(comparing(Object::toString))).isEqualTo(list("a"));
    }

    @Test
    public void two_elements_list() {
      assertThat(list("b", "a").sortUsing(comparing(Object::toString))).isEqualTo(list("a", "b"));
    }

    @Test
    public void many_elements_list() {
      assertThat(list("b", "a", "e", "f", "c", "d", "g").sortUsing(comparing(Object::toString)))
          .isEqualTo(list("a", "b", "c", "d", "e", "f", "g"));
    }
  }

  @Nested
  class _map {
    @Test
    public void returns_empty_list_for_empty_arg() {
      assertThat(List.<String>list().map(String::toUpperCase)).isEmpty();
    }

    @Test
    public void returns_mapped_one_element() {
      assertThat(list("abc").map(String::toUpperCase)).containsExactly("ABC").inOrder();
    }

    @Test
    public void mapping_with_two_element() {
      assertThat(list("abc", "def").map(String::toUpperCase))
          .containsExactly("ABC", "DEF")
          .inOrder();
    }

    @Test
    public void exception_from_mapper_is_propagated() {
      assertCall(() -> list("abc").map(this::throwRuntimeException))
          .throwsException(RuntimeException.class);
    }

    private Object throwRuntimeException(String string) {
      throw new RuntimeException();
    }
  }

  @Nested
  class _filter {
    @Test
    public void returns_empty_for_empty_list() {
      assertThat(list().filter(x -> true)).isEmpty();
    }

    @Test
    public void returns_unmodified_list_when_predicate_is_always_true() {
      assertThat(list("first", "second", "third").filter(x -> true))
          .containsExactly("first", "second", "third")
          .inOrder();
    }

    @Test
    public void returns_empty_list_when_predicate_is_always_false() {
      assertThat(list("first", "second", "third").filter(x -> false)).isEmpty();
    }

    @Test
    public void leaves_only_elements_matching_predicate() {
      assertThat(list("first", "second", "third").filter(s -> s.startsWith("s")))
          .containsExactly("second")
          .inOrder();
    }

    @Test
    public void propagates_exception_thrown_from_function() {
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
    void with_empty_iterable_returns_empty_list() {
      assertThat(list("abc").zip(list(), (x, y) -> "")).containsExactly().inOrder();
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
    void with_shorter_iterable_returns_zipped_with_iterable_size() {
      assertThat(list("a", "b").zip(list("1"), (x, y) -> x + y))
          .containsExactly("a1")
          .inOrder();
    }

    @Test
    void with_longer_iterable_returns_zipped_with_this_size() {
      assertThat(list("a").zip(list("1", "2"), (x, y) -> x + y))
          .containsExactly("a1")
          .inOrder();
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

  @Nested
  class _pull_up_maybe {
    @Test
    public void with_zero_elements() {
      assertThat(pullUpMaybe(list())).isEqualTo(some(list()));
    }

    @Test
    public void with_none() {
      assertThat(pullUpMaybe(list(some("abc"), none()))).isEqualTo(none());
    }

    @Test
    public void with_all_elements_present() {
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
