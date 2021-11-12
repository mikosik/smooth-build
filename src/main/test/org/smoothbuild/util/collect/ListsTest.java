package org.smoothbuild.util.collect;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.allMatch;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.filter;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.mapM;
import static org.smoothbuild.util.collect.Lists.sane;
import static org.smoothbuild.util.collect.Lists.skip;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;
import static org.smoothbuild.util.collect.Lists.zip;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

public class ListsTest {
  @Nested
  class _list {
    @Test
    public void with_no_elements(){
      assertThat(list())
          .isEmpty();
    }

    @Test
    public void with_one_element(){
      assertThat(list("abc"))
          .containsExactly("abc");
    }

    @Test
    public void with_two_elements(){
      assertThat(list("abc", "def"))
          .containsExactly("abc", "def")
          .inOrder();
    }

    @Test
    public void with_three_elements(){
      assertThat(list("abc", "def", "ghi"))
          .containsExactly("abc", "def", "ghi")
          .inOrder();
    }
  }

  @Nested
  class _concat {
    @Nested
    class _single_first {
      @Test
      public void with_empty(){
        assertThat(concat("element", new ArrayList<>()))
            .containsExactly("element");
      }

      @Test
      public void with_non_empty(){
        assertThat(concat("first", asList("second")))
            .containsExactly("first", "second")
            .inOrder();
      }

      @Test
      public void concat_doesnt_modify_list(){
        List<String> list = asList("second");
        concat("first", list);
        assertThat(list)
            .containsExactly("second");
      }

      @Test
      public void first_can_be_subtype_of_list_elements(){
        Integer one = 1;
        Integer two = 2;
        List<Number> list = asList(two);
        assertThat(concat(one, list))
            .containsExactly(one, two);
      }

      @Test
      public void list_element_type_can_be_subtype_of_first(){
        Integer one = 1;
        Integer two = 2;
        List<Integer> list = asList(two);
        assertThat(concat((Number) one, list))
            .containsExactly(one, two);
      }
    }

    @Nested
    class _single_last {
      @Test
      public void with_empty(){
        assertThat(concat(new ArrayList<>(), "element"))
            .containsExactly("element");
      }

      @Test
      public void with_non_empty(){
        assertThat(concat(asList("first"), "second"))
            .containsExactly("first", "second")
            .inOrder();
      }

      @Test
      public void concat_doesnt_modify_list(){
        List<String> list = asList("first");
        concat(list, "second");
        assertThat(list)
            .containsExactly("first");
      }

      @Test
      public void last_can_be_subtype_of_list_element_types(){
        Integer one = 1;
        Integer two = 2;
        List<Number> list = asList(one);
        assertThat(concat(list, two))
            .containsExactly(one, two);
      }

      @Test
      public void list_element_type_can_be_subtype_of_last_type(){
        Integer one = 1;
        Integer two = 2;
        List<Integer> list = asList(one);
        assertThat(concat(list, (Number) two))
            .containsExactly(one, two);
      }
    }

    @Nested
    class _two_iterables {
      @Test
      public void both_empty(){
        assertThat(concat(new ArrayList<>(), new ArrayList<>()))
            .isEmpty();
      }

      @Test
      public void first_empty(){
        assertThat(concat(new ArrayList<>(), asList("second")))
            .containsExactly("second")
            .inOrder();
      }

      @Test
      public void second_empty(){
        assertThat(concat(asList("first"), new ArrayList<>()))
            .containsExactly("first")
            .inOrder();
      }

      @Test
      public void concat_doesnt_modify_lists(){
        List<String> first = asList("first");
        List<String> second = asList("second");
        concat(first, second);
        assertThat(first)
            .containsExactly("first");
        assertThat(second)
            .containsExactly("second");
      }

      @Test
      public void first_can_be_subtype_of_list_elements(){
        Integer one = 1;
        Integer two = 2;
        List<Number> first = asList(one);
        List<Integer> second = asList(two);
        ImmutableList<Number> result = concat(first, second);
        assertThat(result)
            .containsExactly(one, two);
      }

      @Test
      public void second_can_be_subtype_of_list_elements(){
        Integer one = 1;
        Integer two = 2;
        List<Integer> first = asList(one);
        List<Number> second = asList(two);
        ImmutableList<Number> result = concat(first, second);
        assertThat(result)
            .containsExactly(one, two);
      }
    }
  }

  @Nested
  class _skip {
    @Test
    public void returns_same_list_when_skipping_zero(){
      assertThat(skip(0, list("first", "second", "third")))
          .isEqualTo(list("first", "second", "third"));
    }

    @Test
    public void returns_without_first_element_when_skipping_one(){
      assertThat(skip(1, list("first", "second", "third")))
          .isEqualTo(list("second", "third"));
    }

    @Test
    public void returns_empty_when_all_are_skipped(){
      assertThat(skip(3, list("first", "second", "third")))
          .isEqualTo(list());
    }

    @Test
    public void throws_exception_when_to_skip_is_greater_than_list_size(){
      assertCall(() -> skip(4, list("first", "second", "third")))
          .throwsException(IndexOutOfBoundsException.class);
    }
  }

  @Nested
  class _filter {
    @Test
    public void returns_empty_for_empty_list(){
      assertThat(filter(new ArrayList<>(), x -> true))
          .isEmpty();
    }

    @Test
    public void returns_unmodified_list_when_predicate_is_always_true(){
      assertThat(filter(asList("first", "second", "third"), x -> true))
          .containsExactly("first", "second", "third")
          .inOrder();
    }

    @Test
    public void returns_empty_list_when_predicate_is_always_false(){
      assertThat(filter(asList("first", "second", "third"), x -> false))
          .isEmpty();
    }

    @Test
    public void leaves_only_elements_matching_predicate(){
      assertThat(filter(asList("first", "second", "third"), s -> s.startsWith("s")))
          .containsExactly("second")
          .inOrder();
    }
  }

  @Nested
  class _map {
    @Test
    public void returns_empty_list_for_empty_argument(){
      assertThat(map(new ArrayList<String>(), String::toUpperCase))
          .isEmpty();
    }

    @Test
    public void returns_mapped_one_element(){
      assertThat(map(asList("abc"), String::toUpperCase))
          .containsExactly("ABC");
    }

    @Test
    public void mapping_with_two_elements(){
      assertThat(map(asList("abc", "def"), String::toUpperCase))
          .containsExactly("ABC", "DEF");
    }
  }

  @Nested
  class _mapM {
    @Test
    public void returns_empty_list_for_empty_argument(){
      assertThat(mapM(new ArrayList<String>(), String::toUpperCase))
          .isEmpty();
    }

    @Test
    public void returns_mapped_one_element(){
      assertThat(mapM(asList("abc"), String::toUpperCase))
          .containsExactly("ABC");
    }

    @Test
    public void mapping_with_two_elements(){
      assertThat(mapM(asList("abc", "def"), String::toUpperCase))
          .containsExactly("ABC", "DEF");
    }
  }

  @Nested
  class _zip {
    @Test
    public void empty_lists_zip_to_empty_list() {
      assertThat(zip(list(), list(), (String a, String b) -> a))
          .isEqualTo(list());
    }

    @Test
    public void first_list_longer_than_second__causes_exception() {
      assertCall(() -> zip(list(), list("a"), (String a, String b) -> a))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void first_list_shorter_than_second__causes_exception() {
      assertCall(() -> zip(list("a"), list(), (String a, String b) -> a))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void lists_elements_are_zipped_together() {
      assertThat(zip(list("a", "b"), list("1", "2"), (String a, String b) -> a + b))
          .isEqualTo(list("a1", "b2"));
    }

    @Test
    public void different_type_lists_elements_are_zipped_together() {
      assertThat(zip(list("a", "b"), list(1, 2), (String a, Integer b) -> a + b))
          .isEqualTo(list("a1", "b2"));
    }
  }

  @Nested
  class _allMatch {
    @Test
    public void empty_lists_match() {
      assertThat(allMatch(list(), list(), alwaysFalsePredicate()))
          .isTrue();
    }

    @Test
    public void same_size_lists_with_same_content_matches() {
      assertThat(allMatch(list("aaa"), list("aaa"), String::equals))
          .isTrue();
    }

    @Test
    public void list_which_beginning_is_equal_to_other_list_does_not_match_it() {
      assertThat(allMatch(list("aaa"), list("aaa", "bbb"), String::equals))
          .isFalse();
    }

    @Test
    public void non_empty_list_does_not_match_empty() {
      assertThat(allMatch(list("aaa"), list(), alwaysTruePredicate()))
          .isFalse();
    }

    @Test
    public void empty_list_does_not_match_non_empty() {
      assertThat(allMatch(list(), list("aaa"), alwaysTruePredicate()))
          .isFalse();
    }
  }

  @Nested
  class _all_match_otherwise {
    @Test
    public void handlers_are_not_called_when_lists_are_equal() {
      allMatchOtherwise(
          list("aaa"),
          list("aaa"),
          String::equals,
          (i, j) -> { throw new IllegalArgumentException(); },
          i -> { throw new IllegalStateException(); }
      );
    }

    @Test
    public void different_size_handler_is_called_when_listA_size_is_greater() {
      assertCall(() ->
          allMatchOtherwise(
              list("aaa", "bbb"),
              list("aaa"),
              String::equals,
              (i, j) -> { throw new IllegalArgumentException("" + i + ", " + j); },
              i -> { throw new IllegalStateException(); }
          )).throwsException(new IllegalArgumentException("2, 1"));
    }

    @Test
    public void different_size_handler_is_called_when_listB_size_is_greater() {
      assertCall(() ->
          allMatchOtherwise(
              list("aaa"),
              list("aaa", "bbb"),
              String::equals,
              (i, j) -> { throw new IllegalArgumentException("" + i + ", " + j); },
              i -> { throw new IllegalStateException(); }
          )).throwsException(new IllegalArgumentException("1, 2"));
    }

    @Test
    public void elements_dont_match_handler_is_called_when_elements_differ() {
      assertCall(() ->
          allMatchOtherwise(
              list("aaa", "bbb"),
              list("aaa", "ccc"),
              String::equals,
              (i, j) -> { throw new IllegalArgumentException(); },
              i -> { throw new IllegalStateException(Integer.toString(i)); }
          )).throwsException(new IllegalStateException("1"));
    }
  }

  @Nested
  class _sane {
    @Test
    public void converts_null_to_empty_list(){
      assertThat(sane(null))
          .isEmpty();
    }

    @Test
    public void returns_empty_list_for_empty_list_argument(){
      assertThat(sane(new ArrayList<>()))
          .isEmpty();
    }

    @Test
    public void returns_unchanged_list_when_it_has_elements(){
      assertThat(sane(asList("abc", "def")))
          .containsExactly("abc", "def")
          .inOrder();
    }
  }

  private static <T> BiFunction<T, T, Boolean> alwaysFalsePredicate() {
    return (a, b) -> false;
  }

  private static <T> BiFunction<T, T, Boolean> alwaysTruePredicate() {
    return (a, b) -> true;
  }

  @Nested
  class _to_comma_separated_string {
    @Nested
    class _with_function {
      @Test
      public void empty_list(){
        assertThat(toCommaSeparatedString(asList(), String::trim))
            .isEqualTo("");
      }

      @Test
      public void one_element_list(){
        assertThat(toCommaSeparatedString(asList(" one "), String::trim))
            .isEqualTo("one");
      }

      @Test
      public void two_elements_list(){
        assertThat(toCommaSeparatedString(asList(" one ", " two "), String::trim))
            .isEqualTo("one,two");
      }

      @Test
      public void three_elements_list(){
        assertThat(toCommaSeparatedString(asList(" one ", " two ", " three "), String::trim))
            .isEqualTo("one,two,three");
      }
    }

    @Nested
    class _without_function {
      @Test
      public void empty_list(){
        assertThat(toCommaSeparatedString(asList()))
            .isEqualTo("");
      }

      @Test
      public void one_element_list(){
        assertThat(toCommaSeparatedString(asList(1)))
            .isEqualTo("1");
      }

      @Test
      public void two_elements_list(){
        assertThat(toCommaSeparatedString(asList(1, 2)))
            .isEqualTo("1,2");
      }

      @Test
      public void three_elements_list(){
        assertThat(toCommaSeparatedString(asList(1, 2, 3)))
            .isEqualTo("1,2,3");
      }
    }
  }
}

