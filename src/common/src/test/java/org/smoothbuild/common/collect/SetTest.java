package org.smoothbuild.common.collect;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Comparator.naturalOrder;
import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SetTest {
  @Nested
  class _factory_method {
    @Test
    void with_no_arguments() {
      var set = set();
      assertThat(set).isEqualTo(java.util.Set.of());
    }

    @Test
    void with_one_argument() {
      var set = set("1");
      assertThat(set).isEqualTo(java.util.Set.of("1"));
    }

    @Test
    void with_two_arguments() {
      var set = set("1", "2");
      assertThat(set).isEqualTo(java.util.Set.of("1", "2"));
    }

    @Test
    void removes_duplicates() {
      var set = set("1", "2", "1");
      assertThat(set).isEqualTo(java.util.Set.of("1", "2"));
    }
  }

  @Nested
  class _modifying_set_by_calling {
    @Test
    void add() {
      var set = set("1", "2", "3");
      assertCall(() -> set.add("4")).throwsException(UnsupportedOperationException.class);
    }

    @Test
    void addAll() {
      var set = set("1", "2", "3");
      assertCall(() -> set.addAll(java.util.Set.of("4", "5")))
          .throwsException(UnsupportedOperationException.class);
    }

    @Test
    void clear() {
      var set = set("1", "2", "3");
      assertCall(set::clear).throwsException(UnsupportedOperationException.class);
    }

    @Test
    void remove() {
      var set = set("1", "2", "3");
      assertCall(() -> set.remove("1")).throwsException(UnsupportedOperationException.class);
    }

    @Test
    void removeIf() {
      var set = set("1", "2", "3");
      assertCall(() -> set.removeIf((e) -> true))
          .throwsException(UnsupportedOperationException.class);
    }

    @Test
    void removeAll() {
      var set = set("1", "2", "3");
      assertCall(() -> set.removeAll(java.util.Set.of("1", "2")))
          .throwsException(UnsupportedOperationException.class);
    }

    @Test
    void retainAll() {
      var set = set("1", "2", "3");
      assertCall(() -> set.retainAll(java.util.Set.of("1", "2")))
          .throwsException(UnsupportedOperationException.class);
    }

    @Test
    void iterator_remove() {
      var set = set("1", "2", "3");
      var iterator = set.iterator();
      iterator.next();
      assertCall(iterator::remove).throwsException(UnsupportedOperationException.class);
    }

    @Test
    void stream_iterator_remove() {
      var set = set("1", "2", "3");
      var iterator = set.stream().iterator();
      iterator.next();
      assertCall(iterator::remove).throwsException(UnsupportedOperationException.class);
    }
  }

  @Nested
  class _unionWith {
    @Test
    void empty_set_union_with_empty() {
      Set<String> set = set();
      assertThat(set.unionWith(java.util.List.of())).isEmpty();
    }

    @Test
    void empty_set_union_with_elements() {
      Set<String> set = set();
      assertThat(set.unionWith(java.util.List.of("1", "2"))).containsExactly("1", "2");
    }

    @Test
    void non_empty_set_union_with_empty() {
      Set<String> set = set("1", "2");
      assertThat(set.unionWith(java.util.List.of())).containsExactly("1", "2");
    }

    @Test
    void non_empty_set_union_with_non_empty() {
      Set<String> set = set("1", "2");
      assertThat(set.unionWith(java.util.List.of("3", "4"))).containsExactly("1", "2", "3", "4");
    }
  }

  @Nested
  class _map {
    @Test
    void empty_set_returns_empty_set() {
      Set<String> set = set();
      assertThat(set.map(String::toString)).containsExactly().inOrder();
    }

    @Test
    void sorts_elements() {
      var set = set("a", "b", "c");
      assertThat(set.map(String::toUpperCase)).containsExactly("A", "B", "C").inOrder();
    }

    @Test
    void propagates_exception_from_mapper() {
      var set = set("a", "b", "c");
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
      assertThat(set.filter(e -> !e.equals("2"))).isEmpty();
    }

    @Test
    void remove_not_matching_elements() {
      var set = set("1", "2", "3", "4", "5", "6");
      assertThat(set.filter(e -> !e.equals("2")))
          .containsExactly("1", "3", "4", "5", "6")
          .inOrder();
    }

    @Test
    void propagates_exception_from_predicate() {
      var set = set("1", "2", "3");
      var exception = new Exception("message");
      assertCall(() -> set.filter(e -> {
            throw exception;
          }))
          .throwsException(exception);
    }
  }

  @Nested
  class _withRemovedAll {
    @Test
    void on_empty_set_returns_empty_set() {
      Set<String> set = set();
      assertThat(set.withRemovedAll(java.util.Set.of("1", "2"))).isEmpty();
    }

    @Test
    void removes_elements() {
      var set = set("1", "2", "3", "4", "5", "6");
      assertThat(set.withRemovedAll(java.util.Set.of("2", "3")))
          .containsExactly("1", "4", "5", "6")
          .inOrder();
    }
  }

  @Nested
  class _sort {
    @Test
    void empty_set_returns_empty_set() {
      Set<String> set = set();
      assertThat(set.sort(naturalOrder())).containsExactly().inOrder();
    }

    @Test
    void sorts_elements() {
      var set = set("2", "1", "3");
      assertThat(set.sort(naturalOrder())).containsExactly("1", "2", "3").inOrder();
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
      var set = set("1", "2", "3");
      assertThat(set.toList()).containsExactly("1", "2", "3").inOrder();
    }

    @Test
    void returns_instance_of_list() {
      var set = set("1", "2", "3");
      assertThat(set.toList()).isInstanceOf(List.class);
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
        .addEqualityGroup(set("a"), set("a"), set("a", "a"))
        .addEqualityGroup(set("x"), Set.<Object>set("x"))
        .addEqualityGroup(set("a", "b"), set("a", "b"))
        .addEqualityGroup(set("a", "b", "c"), set("a", "b", "c"))
        .testEquals();
  }
}
