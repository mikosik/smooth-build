package org.smoothbuild.util.collect;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Maps.toMap;
import static org.smoothbuild.util.collect.NList.nlist;
import static org.smoothbuild.util.collect.NList.nlistWithShadowing;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

public class NListTest {
  private static final Named n0 = named("zero");
  private static final Named n2 = named("two");
  private static final Named n1 = named("one");

  @Nested
  class _constructing {
    @Test
    public void no_args() {
      assertThat(nlist())
          .isEmpty();
    }

    @Test
    public void varargs() {
      assertThat(nlist(n0, n1, n2))
          .containsExactly(n0, n1, n2)
          .inOrder();
    }

    @Test
    public void from_list() {
      assertThat(nlist(list(n0, n1, n2)))
          .containsExactly(n0, n1, n2)
          .inOrder();
    }

    @Test
    public void from_map() {
      assertThat(nlist(toMap(list(n0, n1, n2), Nameable::nameSane, v -> v)))
          .containsExactly(n0, n1, n2)
          .inOrder();
    }

    @Nested
    class _from_list_with_shadowing {
      @Test
      public void using_non_unique_names_factory_method() {
        assertThat(nlistWithShadowing(list(n0, n1, n2, named(n0.nameSane()))))
            .containsExactly(n0, n1, n2, named(n0.nameSane()))
            .inOrder();
      }

      @Test
      public void using_normal_factory_method_fails() {
        assertCall(() -> nlist(list(n0, n1, n2, named(n0.name()))))
            .throwsException(new IllegalArgumentException(
                "List contains two elements with same name = \"zero\"."));
      }
    }
  }

  @Nested
  class _constructing_lazily {
    @Test
    public void suppliers_are_not_called_by_constructor() {
      new NList<>(this::throwException, this::throwException, this::throwException);
    }

    @Test
    public void list_related_methods_dont_call_map_and_indexMap_suppliers() {
      var nlist = new NList<>(
          () -> list(n0, n1, n2),
          this::throwException,
          this::throwException);
      nlist.map(e -> e);
      nlist.equals(nlist);
      nlist.hashCode();
      nlist.toString();
      nlist.valuesToString();
      nlist.get(0);
      nlist.size();
      nlist.forEach(e -> {});
      nlist.spliterator();
      nlist.stream();
      nlist.parallelStream();
      nlist.toArray(Object[]::new);
    }

    @Test
    public void map_related_methods_dont_call_list_and_indexMap_suppliers() {
      var nlist = new NList<>(
          this::throwException,
          () -> ImmutableMap.of(n1.nameSane(), n1),
          this::throwException);
      nlist.containsName("name");
      nlist.get("name");
    }

    @Test
    public void index_of_method_doesnt_call_list_and_map_suppliers() {
      var nlist = new NList<>(
          this::throwException,
          this::throwException,
          () -> ImmutableMap.of("name", 1));
      nlist.indexOf("name");
    }

    private <T> T throwException() {
      throw new RuntimeException();
    }
  }

  @Nested
  @SuppressWarnings("deprecation")
  class _mutable_methods_throw_exception {
    @Test
    public void removeIf() {
      var nlist = list(n0, n1, n2);
      assertCall(() -> nlist.removeIf(n -> true))
          .throwsException(UnsupportedOperationException.class);
    }

    @Test
    public void replaceAll() {
      var nlist = list(n0, n1, n2);
      assertCall(() -> nlist.replaceAll(n -> n))
          .throwsException(UnsupportedOperationException.class);
    }

    @Test
    public void sort() {
      var nlist = list(n0, n1, n2);
      assertCall(() -> nlist.sort((a, b) -> 0))
          .throwsException(UnsupportedOperationException.class);
    }
  }

  @Nested
  class _map {
    @Test
    public void maps_elements() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.map(v -> named(v.name().concat("!"))))
          .containsExactly(named("zero!"), named("one!"), named("two!"))
          .inOrder();
    }
  }

  @Nested
  class _get_name {
    @Test
    public void returns_element_with_given_name() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.get(n0.nameSane()))
          .isEqualTo(n0);
    }

    @Test
    public void returns_null_when_element_with_given_name_doesnt_exist() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.get("seven"))
          .isNull();
    }

    @Test
    public void returns_first_occurrence_when_more_than_one_element_has_given_name() {
      var nlist = nlistWithShadowing(list(n0, n1, n2, named(n0.name())));
      assertThat(nlist.get(n0.nameSane()))
          .isSameInstanceAs(n0);
    }
  }

  @Nested
  class _contains_name {
    @Test
    public void returns_true_when_element_with_given_name_exists() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.containsName(n0.nameSane()))
          .isTrue();
    }

    @Test
    public void returns_false_when_element_with_given_name_doesnt_exist() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.containsName("seven"))
          .isFalse();
    }
  }

  @Nested
  class _to_string {
    @Test
    public void to_string() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.toString())
          .isEqualTo("NList(zero,one,two)");
    }
  }

  @Nested
  class _values_to_string {
    @Test
    public void values_to_string() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.valuesToString())
          .isEqualTo("zero,one,two");
    }
  }

  @Nested
  class _values_to_pretty_string {
    @Test
    public void values_to_string() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.valuesToPrettyString())
          .isEqualTo("""
              zero
              one
              two""");
    }
  }

  @Nested
  class _get_index {
    @Test
    public void returns_element_at_given_index() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.get(1))
          .isEqualTo(n1);
    }

    @Test
    public void throws_exception_when_index_out_of_bounds() {
      var nlist = nlist(n0, n1, n2);
      assertCall(() -> nlist.get(3))
          .throwsException(ArrayIndexOutOfBoundsException.class);
    }
  }


  @Nested
  class _index_of {
    @Test
    public void unique_names() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.indexOf(n0.nameSane()))
          .isEqualTo(0);
    }

    @Test
    public void non_unique_names() {
      var nlist = nlistWithShadowing(list(n0, n1, n2, named(n0.name())));
      assertThat(nlist.indexOf(n0.nameSane()))
          .isEqualTo(0);
    }
  }

  private static Named named(String name) {
    return new MyNamed(name);
  }

  private static record MyNamed(String name) implements Named {
    @Override
    public String toString() {
      return name;
    }
  }
}
