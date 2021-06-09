package org.smoothbuild.util;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.allMatch;
import static org.smoothbuild.util.Lists.concat;
import static org.smoothbuild.util.Lists.filter;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.mapM;
import static org.smoothbuild.util.Lists.sane;
import static org.smoothbuild.util.Lists.zip;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ListsTest {
  @Nested
  class list {
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
  class concat {
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
    }
  }

  @Nested
  class filter {
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
  class map {
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
  class mapM {
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
      assertThat(zip(List.of(), List.of(), (String a, String b) -> a))
          .isEqualTo(List.of());
    }

    @Test
    public void first_list_longer_than_second__causes_exception() {
      assertCall(() -> zip(List.of(), List.of("a"), (String a, String b) -> a))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void first_list_shorter_than_second__causes_exception() {
      assertCall(() -> zip(List.of("a"), List.of(), (String a, String b) -> a))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void lists_elements_are_zipped_together() {
      assertThat(zip(List.of("a", "b"), List.of("1", "2"), (String a, String b) -> a + b))
          .isEqualTo(List.of("a1", "b2"));
    }

    @Test
    public void different_type_lists_elements_are_zipped_together() {
      assertThat(zip(List.of("a", "b"), List.of(1, 2), (String a, Integer b) -> a + b))
          .isEqualTo(List.of("a1", "b2"));
    }
  }

  @Nested
  class _allMatch {
    @Test
    public void empty_lists_match() {
      assertThat(allMatch(List.of(), List.of(), alwaysFalsePredicate()))
          .isTrue();
    }

    @Test
    public void same_size_lists_with_same_content_matches() {
      assertThat(allMatch(List.of("aaa"), List.of("aaa"), String::equals))
          .isTrue();
    }

    @Test
    public void list_which_beginning_is_equal_to_other_list_does_not_match_it() {
      assertThat(allMatch(List.of("aaa"), List.of("aaa", "bbb"), String::equals))
          .isFalse();
    }

    @Test
    public void non_empty_list_does_not_match_empty() {
      assertThat(allMatch(List.of("aaa"), List.of(), alwaysTruePredicate()))
          .isFalse();
    }

    @Test
    public void empty_list_does_not_match_non_empty() {
      assertThat(allMatch(List.of(), List.of("aaa"), alwaysTruePredicate()))
          .isFalse();
    }

    private <T> BiFunction<T, T, Boolean> alwaysFalsePredicate() {
      return (a, b) -> false;
    }

    private <T> BiFunction<T, T, Boolean> alwaysTruePredicate() {
      return (a, b) -> true;
    }
  }

  @Nested
  class sane {
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
}
