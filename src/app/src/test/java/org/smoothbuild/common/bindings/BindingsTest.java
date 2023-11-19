package org.smoothbuild.common.bindings;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.bindings.Bindings.mutableBindings;
import static org.smoothbuild.common.collect.Lists.list;
import static org.smoothbuild.common.collect.Maps.toMap;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BindingsTest {
  @Nested
  class _flat_immutable_bindings {
    @Nested
    class _bindings_tests extends AbstractBindingsTestSuite {
      @Override
      public Bindings<Elem> newBindings(Elem... elems) {
        return immutableBindings(mapOfElems(elems));
      }
    }
  }

  @Nested
  class _flat_mutable_bindings {
    @Nested
    class _bindings_tests extends AbstractBindingsTestSuite {
      @Override
      public Bindings<Elem> newBindings(Elem... elems) {
        return mutableBindingsWith(elems);
      }
    }

    @Nested
    class _mutable_bindings_tests extends AbstractMutableBindingsTestSuite {
      @Override
      public MutableBindings<Integer> newBindings() {
        return mutableBindings();
      }
    }
  }

  @Nested
  class _scoped_immutable_bindings {
    @Nested
    class _scoped_bindings_tests extends AbstractScopedBindingsTestSuite {
      @Override
      protected Bindings<Elem> newBindings(Bindings<Elem> outerScope, Elem... elems) {
        return mutableBindingsWith(outerScope, elems);
      }

      @Override
      protected Bindings<Elem> newBindingsWithInnerScopeWith(Elem... elems) {
        return immutableBindings(immutableBindings(), mapOfElems(elems));
      }

      @Override
      protected Bindings<Elem> newBindingsWithOuterScopeWith(Elem... elems) {
        return immutableBindings(immutableBindings(mapOfElems(elems)), mapOfElems());
      }
    }
  }

  @Nested
  class _scoped_mutable_bindings {
    @Nested
    class _scoped_bindings_tests extends AbstractScopedBindingsTestSuite {
      @Override
      protected Bindings<Elem> newBindings(Bindings<Elem> outerScope, Elem... elems) {
        return mutableBindingsWith(outerScope, elems);
      }

      @Override
      protected Bindings<Elem> newBindingsWithInnerScopeWith(Elem... elems) {
        return mutableBindingsWith(immutableBindings(), elems);
      }

      @Override
      protected Bindings<Elem> newBindingsWithOuterScopeWith(Elem... elems) {
        return mutableBindingsWith(immutableBindings(mapOfElems(elems)));
      }
    }

    @Nested
    class _mutable_bindings_tests extends AbstractMutableBindingsTestSuite {
      @Override
      public MutableBindings<Integer> newBindings() {
        return mutableBindings(immutableBindings());
      }
    }
  }

  @Test
  public void equals_and_hashcode() {
    var elem1 = elem("1", 1);
    var elem2 = elem("2", 2);

    var equalsTester = new EqualsTester();
    // flat bindings with no elements
    equalsTester.addEqualityGroup(
        immutableBindings(), immutableBindings(Map.of()), mutableBindings());

    // flat bindings with elem1
    equalsTester.addEqualityGroup(immutableBindingsWith(elem1), mutableBindingsWith(elem1));

    // flat bindings with elem2
    equalsTester.addEqualityGroup(immutableBindingsWith(elem2), mutableBindingsWith(elem2));

    // scoped bindings with elem1 in outer scope
    equalsTester.addEqualityGroup(
        immutableBindings(immutableBindingsWith(elem1), Map.of()),
        mutableBindings(immutableBindingsWith(elem1)));

    // scoped bindings with elem1 in inner scope
    equalsTester.addEqualityGroup(
        immutableBindings(immutableBindings(), mapOfElems(elem1)),
        mutableBindingsWith(immutableBindings(), elem1));

    // scoped bindings with elem1 in outer and inner scope
    equalsTester.addEqualityGroup(
        immutableBindings(immutableBindingsWith(elem1), mapOfElems(elem1)),
        mutableBindingsWith(immutableBindingsWith(elem1), elem1));

    // element-1 in outer scope and element-2 in inner scope
    equalsTester.addEqualityGroup(
        immutableBindings(immutableBindingsWith(elem1), mapOfElems(elem2)),
        mutableBindingsWith(immutableBindingsWith(elem1), elem2));

    equalsTester.testEquals();
  }

  public abstract static class AbstractBindingsTestSuite {
    @Test
    public void getting_element() {
      var bindings = newBindings(elem("name", 7));
      assertThat(bindings.get("name")).isEqualTo(elem("name", 7));
    }

    @Test
    public void getting_missing_element_throws_exception() {
      var bindings = newBindings();
      assertCall(() -> bindings.get("name")).throwsException(new NoSuchElementException("name"));
    }

    @Test
    public void getOptional_element() {
      var bindings = newBindings(elem("name", 7));
      assertThat(bindings.getOptional("name")).isEqualTo(Optional.of(elem("name", 7)));
    }

    @Test
    public void getOptional_missing_element_returns_empty() {
      var bindings = newBindings();
      assertThat(bindings.getOptional("name")).isEqualTo(Optional.empty());
    }

    @Test
    public void contains_present_element() {
      var bindings = newBindings(elem("name", 7));
      assertThat(bindings.contains("name")).isTrue();
    }

    @Test
    public void contains_missing_element_returns_false() {
      var bindings = newBindings();
      assertThat(bindings.contains("name")).isFalse();
    }

    @Test
    public void map() {
      var bindings = newBindings(elem("name", 7), elem("other", 5));
      var mapped = bindings.map(elem -> elem.value);
      assertThat(mapped.get("name")).isEqualTo(7);
    }

    @Test
    public void asMap() {
      var first = elem("name", 7);
      var second = elem("other", 5);
      var bindings = newBindings(first, second);
      assertThat(bindings.toMap()).isEqualTo(Map.of("name", first, "other", second));
    }

    public abstract Bindings<Elem> newBindings(Elem... elems);
  }

  public abstract static class AbstractMutableBindingsTestSuite {
    public abstract MutableBindings<Integer> newBindings();

    @Test
    public void add_return_null_if_binding_is_not_already_present() {
      var mutableBindings = newBindings();
      assertThat(mutableBindings.add("one", 1)).isEqualTo(null);
    }

    @Test
    public void add_overwrites_previous_binding_if_present() {
      var mutableBindings = newBindings();
      mutableBindings.add("name", 1);
      mutableBindings.add("name", 2);
      assertThat(mutableBindings.get("name")).isEqualTo(2);
    }

    @Test
    public void add_return_previous_binding_if_present() {
      var mutableBindings = newBindings();
      mutableBindings.add("name", 1);
      assertThat(mutableBindings.add("name", 2)).isEqualTo(1);
    }
  }

  public abstract static class AbstractScopedBindingsTestSuite {
    protected abstract Bindings<Elem> newBindings(Bindings<Elem> outerScope, Elem... elems);

    protected abstract Bindings<Elem> newBindingsWithInnerScopeWith(Elem... elems);

    protected abstract Bindings<Elem> newBindingsWithOuterScopeWith(Elem... elems);

    @Nested
    class _bindings_tests_for_inner_scope extends AbstractBindingsTestSuite {
      @Override
      public Bindings<Elem> newBindings(Elem... elems) {
        return newBindingsWithInnerScopeWith(elems);
      }
    }

    @Nested
    class _bindings_tests_for_outer_scope extends AbstractBindingsTestSuite {
      @Override
      public Bindings<Elem> newBindings(Elem... elems) {
        return newBindingsWithOuterScopeWith(elems);
      }
    }

    @Nested
    class _to_map {
      @Test
      public void contains_elements_from_outer_and_inner_scope() {
        var outer = immutableBindings(mapOfElems(elem("1", 1)));
        var bindings = newBindings(outer, elem("2", 2));
        assertThat(bindings.toMap()).isEqualTo(mapOfElems(elem("1", 1), elem("2", 2)));
      }

      @Test
      public void does_not_contain_elements_from_outer_scope_overwritten_in_inner_scope() {
        var outer = immutableBindings(mapOfElems(elem("1", 1)));
        var bindings = newBindings(outer, elem("1", 11));
        assertThat(bindings.toMap()).isEqualTo(mapOfElems(elem("1", 11)));
      }
    }

    @Test
    public void element_in_inner_bounds_shadows_element_from_outer_bounds() {
      var outer = immutableBindings(mapOfElems(elem("value-a", 7)));
      var shadowing = elem("value-a", 9);
      var inner = newBindings(outer, shadowing);
      assertThat(inner.get(shadowing.name())).isEqualTo(shadowing);
    }

    @Test
    public void to_string() {
      var outer = immutableBindings(mapOfElems(elem("value-a", 7), elem("value-b", 8)));
      var inner = newBindings(outer, elem("value-c", 9));
      assertThat(inner.toString())
          .isEqualTo(
              """
            value-a -> Elem[name=value-a, value=7]
            value-b -> Elem[name=value-b, value=8]
              value-c -> Elem[name=value-c, value=9]""");
    }
  }

  private static ImmutableBindings<Elem> immutableBindingsWith(Elem elem) {
    return immutableBindings(Map.of(elem.name(), elem));
  }

  private static Bindings<Elem> mutableBindingsWith(Elem... elems) {
    FlatMutableBindings<Elem> bindings = mutableBindings();
    for (Elem elem : elems) {
      bindings.add(elem.name(), elem);
    }
    return bindings;
  }

  private static Bindings<Elem> mutableBindingsWith(
      Bindings<Elem> outerScopeBindings, Elem... elems) {
    var mutableBindings = mutableBindings(outerScopeBindings);
    for (Elem elem : elems) {
      mutableBindings.add(elem.name(), elem);
    }
    return mutableBindings;
  }

  public static ImmutableMap<String, Elem> mapOfElems(Elem... nameables) {
    return toMap(list(nameables), Elem::name, e -> e);
  }

  public static Elem elem(String name, int value) {
    return new Elem(name, value);
  }

  protected static record Elem(String name, Integer value) {}
}
