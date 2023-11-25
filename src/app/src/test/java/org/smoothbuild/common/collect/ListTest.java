package org.smoothbuild.common.collect;

import static com.google.common.truth.Truth.assertThat;
import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.pullUpOption;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import com.google.common.testing.EqualsTester;
import io.vavr.control.Option;
import java.util.Comparator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ListTest {
  @Nested
  class _creating_list {
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

    @Test
    void as_copy_of_other_list() {
      var list = asList("a", "b", "c");
      assertThat(list(list)).isNotSameInstanceAs(list);
    }

    @Test
    void as_copy_returns_argument_when_it_is_instance_of_this_class() {
      var list = list("a", "b", "c");
      assertThat(list(list)).isSameInstanceAs(list);
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

      var copy = list(original);
      original.set(0, "x");

      assertThat(copy).containsExactly("a", "b", "c").inOrder();
    }

    @Test
    void as_copy_of_other_list_creates_different_instance() {
      var list = asList("a", "b", "c");
      assertThat(list(list)).isNotSameInstanceAs(list);
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
  class _append {
    @Test
    void two_empty_lists() {
      assertThat(list().append(asList())).isEqualTo(list());
    }

    @Test
    void empty_list() {
      assertThat(list("a").append(asList())).isEqualTo(list("a"));
    }

    @Test
    void to_empty_list() {
      assertThat(list().append(asList("b"))).isEqualTo(list("b"));
    }

    @Test
    void two_one_element_lists() {
      assertThat(list("a").append(asList("b"))).isEqualTo(list("a", "b"));
    }

    @Test
    void element_lists() {
      assertThat(list("a", "b", "c").append(asList("d", "e", "f")))
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
  class _pull_up_list {
    @Test
    public void with_zero_elements() {
      assertThat(pullUpOption(list())).isEqualTo(Option.of(list()));
    }

    @Test
    public void with_none() {
      assertThat(pullUpOption(list(Option.of("abc"), none()))).isEqualTo(none());
    }

    @Test
    public void with_all_elements_present() {
      assertThat(pullUpOption(list(some("abc"), some("def")))).isEqualTo(some(list("abc", "def")));
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
