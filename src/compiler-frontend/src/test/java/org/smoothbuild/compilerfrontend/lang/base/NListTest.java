package org.smoothbuild.compilerfrontend.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.compilerfrontend.lang.base.Id.id;
import static org.smoothbuild.compilerfrontend.lang.base.NList.nlist;
import static org.smoothbuild.compilerfrontend.lang.base.NList.nlistWithShadowing;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class NListTest {
  private static final Identifiable n0 = named("zero");
  private static final Identifiable n1 = named("one");
  private static final Identifiable n2 = named("two");

  @Nested
  class _constructing {
    @Test
    void no_args() {
      assertThat(nlist()).isEmpty();
    }

    @Test
    void varargs() {
      assertThat(nlist(n0, n1, n2)).containsExactly(n0, n1, n2).inOrder();
    }

    @Test
    void from_list() {
      assertThat(nlist(list(n0, n1, n2))).containsExactly(n0, n1, n2).inOrder();
    }

    @Test
    void from_map() {
      assertThat(nlist(list(n0, n1, n2).toMap(Identifiable::id, v -> v)))
          .containsExactly(n0, n1, n2)
          .inOrder();
    }

    @Nested
    class _from_list_with_shadowing {
      @Test
      void using_non_unique_names_factory_method() {
        assertThat(nlistWithShadowing(list(n0, n1, n2, named(n0.id()))))
            .containsExactly(n0, n1, n2, named(n0.id()))
            .inOrder();
      }

      @Test
      void using_normal_factory_method_fails() {
        assertCall(() -> nlist(list(n0, n1, n2, named(n0.id()))))
            .throwsException(new IllegalArgumentException(
                "List contains two elements with same name = \"zero\"."));
      }
    }
  }

  @Nested
  class _constructing_lazily {
    @Test
    void suppliers_are_not_called_by_constructor() {
      new NList<>(this::throwException, this::throwException, this::throwException);
    }

    @Test
    void list_related_methods_dont_call_map_and_indexMap_suppliers() {
      var nlist = new NList<>(() -> list(n0, n1, n2), this::throwException, this::throwException);
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
    void map_related_methods_dont_call_list_and_indexMap_suppliers() {
      var nlist = new NList<>(this::throwException, () -> map(n1.id(), n1), this::throwException);
      nlist.containsName(id("name"));
      nlist.get(id("name"));
    }

    @Test
    void index_of_method_doesnt_call_list_and_map_suppliers() {
      var nlist = new NList<>(this::throwException, this::throwException, () -> map(id("name"), 1));
      nlist.indexOf(id("name"));
    }

    private <T> T throwException() {
      throw new RuntimeException();
    }
  }

  @Nested
  @SuppressWarnings("deprecation")
  class _mutable_methods_throw_exception {
    @Test
    void removeIf() {
      var nlist = list(n0, n1, n2);
      assertCall(() -> nlist.removeIf(n -> true))
          .throwsException(UnsupportedOperationException.class);
    }

    @Test
    void replaceAll() {
      var nlist = list(n0, n1, n2);
      assertCall(() -> nlist.replaceAll(n -> n))
          .throwsException(UnsupportedOperationException.class);
    }

    @Test
    void sort() {
      var nlist = list(n0, n1, n2);
      assertCall(() -> nlist.sort((a, b) -> 0))
          .throwsException(UnsupportedOperationException.class);
    }
  }

  @Nested
  class _map {
    @Test
    void maps_elements() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.map(v -> named(v.id().full().concat(":x"))))
          .containsExactly(named("zero:x"), named("one:x"), named("two:x"))
          .inOrder();
    }
  }

  @Nested
  class _get_name {
    @Test
    void returns_element_with_given_name() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.get(n0.id())).isEqualTo(n0);
    }

    @Test
    void returns_null_when_element_with_given_name_doesnt_exist() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.get(id("seven"))).isNull();
    }

    @Test
    void returns_first_occurrence_when_more_than_one_element_has_given_name() {
      var nlist = nlistWithShadowing(list(n0, n1, n2, named(n0.id())));
      assertThat(nlist.get(n0.id())).isSameInstanceAs(n0);
    }
  }

  @Nested
  class _contains_name {
    @Test
    void returns_true_when_element_with_given_name_exists() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.containsName(n0.id())).isTrue();
    }

    @Test
    void returns_false_when_element_with_given_name_doesnt_exist() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.containsName(id("seven"))).isFalse();
    }
  }

  @Nested
  class _to_string {
    @Test
    void to_string() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.toString()).isEqualTo("NList(zero,one,two)");
    }
  }

  @Nested
  class _values_to_string {
    @Test
    void values_to_string() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.valuesToString()).isEqualTo("zero,one,two");
    }
  }

  @Nested
  class _values_to_pretty_string {
    @Test
    void values_to_string() {
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
    void returns_element_at_given_index() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.get(1)).isEqualTo(n1);
    }

    @Test
    void throws_exception_when_index_out_of_bounds() {
      var nlist = nlist(n0, n1, n2);
      assertCall(() -> nlist.get(3)).throwsException(ArrayIndexOutOfBoundsException.class);
    }
  }

  @Nested
  class _index_of {
    @Test
    void unique_names() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.indexOf(n0)).isEqualTo(0);
    }

    @Test
    void non_unique_names() {
      var nlist = nlistWithShadowing(list(n0, n1, n2, named(n0.id())));
      assertThat(nlist.indexOf(n0)).isEqualTo(0);
    }
  }

  @Test
  void equals_and_hash_code() {
    new EqualsTester()
        .addEqualityGroup(nlist(), nlist())
        .addEqualityGroup(nlist(named("a")), nlist(named("a")))
        .addEqualityGroup(nlist(named("b")), nlist(named("b")))
        .addEqualityGroup(nlist(named("a"), named("b")), nlist(named("a"), named("b")));
  }

  private static Identifiable named(String name) {
    return new MyNamed(name);
  }

  private static Identifiable named(Id id) {
    return new MyNamed(id);
  }

  private static record MyNamed(Id id) implements Identifiable {
    public MyNamed(String name) {
      this(Id.id(name));
    }

    @Override
    public String toString() {
      return id.toString();
    }

    @Override
    public Id id() {
      return id;
    }
  }
}
