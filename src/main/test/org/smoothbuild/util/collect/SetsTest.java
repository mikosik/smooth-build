package org.smoothbuild.util.collect;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Sets.filter;
import static org.smoothbuild.util.collect.Sets.map;
import static org.smoothbuild.util.collect.Sets.set;

import java.util.HashSet;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SetsTest {
  @Nested
  class create {
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
  class map {
    @Test
    public void returns_empty_for_empty_argument() {
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
  class filter {
    @Test
    public void returns_empty_for_empty_argument() {
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
}
