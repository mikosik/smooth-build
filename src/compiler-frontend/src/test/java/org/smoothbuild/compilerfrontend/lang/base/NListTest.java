package org.smoothbuild.compilerfrontend.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.compilerfrontend.lang.base.NList.nlist;
import static org.smoothbuild.compilerfrontend.lang.base.NList.nlistWithShadowing;
import static org.smoothbuild.compilerfrontend.lang.base.Name.referenceableName;

import com.google.common.testing.EqualsTester;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class NListTest {
  private static final HasName n0 = hasName("zero");
  private static final HasName n1 = hasName("one");
  private static final HasName n2 = hasName("two");

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
      assertThat(nlist(list(n0, n1, n2).toMap(HasName::name, v -> v)))
          .containsExactly(n0, n1, n2)
          .inOrder();
    }

    @Nested
    class _from_list_with_shadowing {
      @Test
      void using_non_unique_names_factory_method() {
        assertThat(nlistWithShadowing(list(n0, n1, n2, hasName(n0))))
            .containsExactly(n0, n1, n2, hasName(n0))
            .inOrder();
      }

      @Test
      void using_normal_factory_method_fails() {
        assertCall(() -> nlist(list(n0, n1, n2, hasName(n0))))
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
    }

    @Test
    void map_related_methods_dont_call_list_and_indexMap_suppliers() {
      var nlist = new NList<>(this::throwException, () -> map(n1.name(), n1), this::throwException);
      nlist.containsName(name("name"));
      nlist.get(name("name"));
    }

    @Test
    void index_of_method_doesnt_call_list_and_map_suppliers() {
      var nlist =
          new NList<>(this::throwException, this::throwException, () -> map(name("name"), 1));
      nlist.indexOf(name("name"));
    }

    private <T> T throwException() {
      throw new RuntimeException();
    }
  }

  @Nested
  class _mutable_methods_throw_exception {
    @Test
    void removeIf() {
      var nlist = nlist(n0, n1, n2);
      assertCall(() -> nlist.removeIf(n -> true))
          .throwsException(UnsupportedOperationException.class);
    }

    @Test
    void replaceAll() {
      var nlist = nlist(n0, n1, n2);
      assertCall(() -> nlist.replaceAll(n -> n))
          .throwsException(UnsupportedOperationException.class);
    }

    @Test
    void sort() {
      var nlist = nlist(n0, n1, n2);
      assertCall(() -> nlist.sort((a, b) -> 0))
          .throwsException(UnsupportedOperationException.class);
    }
  }

  @Nested
  class _map {
    @Test
    void maps_elements() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.map(v -> hasName(v.name().toString() + "X")))
          .containsExactly(hasName("zeroX"), hasName("oneX"), hasName("twoX"))
          .inOrder();
    }
  }

  @Nested
  class _get_name {
    @Test
    void returns_element_with_given_name() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.get(n0.name())).isEqualTo(n0);
    }

    @Test
    void returns_null_when_element_with_given_name_doesnt_exist() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.get(name("seven"))).isNull();
    }

    @Test
    void returns_first_occurrence_when_more_than_one_element_has_given_name() {
      var nlist = nlistWithShadowing(list(n0, n1, n2, hasName(n0)));
      assertThat(nlist.get(n0.name())).isSameInstanceAs(n0);
    }
  }

  @Nested
  class _contains_name {
    @Test
    void returns_true_when_element_with_given_name_exists() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.containsName(n0.name())).isTrue();
    }

    @Test
    void returns_false_when_element_with_given_name_doesnt_exist() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.containsName(name("seven"))).isFalse();
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
      assertCall(() -> nlist.get(3)).throwsException(NoSuchElementException.class);
    }
  }

  @Nested
  class _index_of {
    @Test
    void unique_names() {
      var nlist = nlist(n0, n1, n2);
      assertThat(nlist.indexOf(n0.name())).isEqualTo(0);
    }

    @Test
    void non_unique_names() {
      var nlist = nlistWithShadowing(list(n0, n1, n2, hasName(n0)));
      assertThat(nlist.indexOf(n0.name())).isEqualTo(0);
    }
  }

  @Test
  void equals_and_hash_code() {
    new EqualsTester()
        .addEqualityGroup(nlist(), nlist())
        .addEqualityGroup(nlist(hasName("a")), nlist(hasName("a")))
        .addEqualityGroup(nlist(hasName("b")), nlist(hasName("b")))
        .addEqualityGroup(nlist(hasName("a"), hasName("b")), nlist(hasName("a"), hasName("b")));
  }

  private static HasName hasName(String name) {
    return new MyHasName(name);
  }

  private static HasName hasName(HasName hasName) {
    return new MyHasName(hasName.name());
  }

  private static record MyHasName(Name name) implements HasName {
    public MyHasName(String name) {
      this(referenceableName(name));
    }

    @Override
    public String toString() {
      return name.toString();
    }
  }

  private static Name name(String name) {
    return referenceableName(name);
  }
}
