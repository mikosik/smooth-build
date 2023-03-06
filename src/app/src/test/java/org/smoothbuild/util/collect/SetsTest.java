package org.smoothbuild.util.collect;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Comparator.comparing;
import static org.smoothbuild.util.collect.Sets.filter;
import static org.smoothbuild.util.collect.Sets.map;
import static org.smoothbuild.util.collect.Sets.set;
import static org.smoothbuild.util.collect.Sets.sort;
import static org.smoothbuild.util.collect.Sets.union;

import java.util.HashSet;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SetsTest {
  @Nested
  class _create {
    @Test
    public void set_with_no_elems() {
      assertThat(set())
          .isEmpty();
    }

    @Test
    public void set_with_one_elem() {
      assertThat(set("abc"))
          .containsExactly("abc");
    }

    @Test
    public void set_with_two_elems() {
      assertThat(set("abc", "def"))
          .containsExactly("abc", "def");
    }

    @Test
    public void set_with_three_elems() {
      assertThat(set("abc", "def", "ghi"))
          .containsExactly("abc", "def", "ghi");
    }
  }

  @Nested
  class _union_with_set {
    @Test
    public void of_empty_sets_is_empty_set() {
      assertThat(union(new HashSet<>(), new HashSet<>()))
          .isEmpty();
    }

    @Test
    public void of_two_sets_contains_value_from_each_one() {
      assertThat(union(set("abc"), set("def")))
          .containsExactly("abc", "def");
    }
  }

  @Nested
  class _union_with_elem {
    @Test
    public void of_empty_sets_is_added_elem() {
      assertThat(union(new HashSet<>(), "abc"))
          .containsExactly("abc");
    }

    @Test
    public void of_set_and_value_contains_set_elements_and_added_element() {
      assertThat(union(set("abc"), "def"))
          .containsExactly("abc", "def");
    }
  }

  @Nested
  class _map {
    @Test
    public void returns_empty_for_empty_arg() {
      assertThat(map(new HashSet<String>(), String::toUpperCase))
          .isEmpty();
    }

    @Test
    public void one_elem() {
      assertThat(map(set("abc"), String::toUpperCase))
          .containsExactly("ABC");
    }

    @Test
    public void two_elems() {
      assertThat(map(set("abc", "def"), String::toUpperCase))
          .containsExactly("ABC", "DEF");
    }
  }

  @Nested
  class _filter {
    @Test
    public void returns_empty_for_empty_arg() {
      assertThat(filter(new HashSet<>(), e -> true))
          .isEmpty();
    }

    @Test
    public void with_always_true_predicate_returns_unchanged_list() {
      assertThat(filter(set("first", "second", "third"), s -> true))
          .containsExactly("first", "second", "third");
    }

    @Test
    public void with_always_false_predicate_returns_empty_list() {
      assertThat(filter(set("first", "second", "third"), s -> false))
          .isEmpty();
    }

    @Test
    public void filter_elems_matching_predicate() {
      assertThat(filter(set("first", "second", "third"), s -> s.startsWith("s")))
          .containsExactly("second");
    }
  }

  @Nested
  class _sort {
    @Test
    public void returns_empty_for_empty_arg() {
      assertThat(sort(new HashSet<>(), comparing(Object::toString)))
          .isEmpty();
    }

    @Test
    public void sorts_elements() {
      assertThat(sort(set(4, 2, 3, 1), Integer::compareTo))
          .isEqualTo(set(1, 2, 3, 4));
    }
  }
}
