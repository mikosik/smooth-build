package org.smoothbuild.common.bindings;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.bindings.Bindings.mutableBindings;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import com.google.common.testing.EqualsTester;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.Map;

public class BindingsTest {
  @Nested
  class _flat_immutable_bindings {
    @Nested
    class _bindings_tests extends AbstractBindingsTestSuite {
      @Override
      public Bindings<Element> newBindings(Element... elements) {
        return immutableBindings(mapOfElems(elements));
      }
    }
  }

  @Nested
  class _flat_mutable_bindings {
    @Nested
    class _bindings_tests extends AbstractBindingsTestSuite {
      @Override
      public Bindings<Element> newBindings(Element... elements) {
        return mutableBindingsWith(elements);
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
      protected Bindings<Element> newBindings(Bindings<Element> outerScope, Element... elements) {
        return mutableBindingsWith(outerScope, elements);
      }

      @Override
      protected Bindings<Element> newBindingsWithInnerScopeWith(Element... elements) {
        return immutableBindings(immutableBindings(), mapOfElems(elements));
      }

      @Override
      protected Bindings<Element> newBindingsWithOuterScopeWith(Element... elements) {
        return immutableBindings(immutableBindings(mapOfElems(elements)), mapOfElems());
      }
    }
  }

  @Nested
  class _scoped_mutable_bindings {
    @Nested
    class _scoped_bindings_tests extends AbstractScopedBindingsTestSuite {
      @Override
      protected Bindings<Element> newBindings(Bindings<Element> outerScope, Element... elements) {
        return mutableBindingsWith(outerScope, elements);
      }

      @Override
      protected Bindings<Element> newBindingsWithInnerScopeWith(Element... elements) {
        return mutableBindingsWith(immutableBindings(), elements);
      }

      @Override
      protected Bindings<Element> newBindingsWithOuterScopeWith(Element... elements) {
        return mutableBindingsWith(immutableBindings(mapOfElems(elements)));
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
  void equals_and_hashcode() {
    var elem1 = elements("1", 1);
    var elem2 = elements("2", 2);

    var equalsTester = new EqualsTester();
    // flat bindings with no elements
    equalsTester.addEqualityGroup(immutableBindings(), immutableBindings(map()), mutableBindings());

    // flat bindings with elem1
    equalsTester.addEqualityGroup(immutableBindingsWith(elem1), mutableBindingsWith(elem1));

    // flat bindings with elem2
    equalsTester.addEqualityGroup(immutableBindingsWith(elem2), mutableBindingsWith(elem2));

    // scoped bindings with elem1 in outer scope
    equalsTester.addEqualityGroup(
        immutableBindings(immutableBindingsWith(elem1), map()),
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
    void getting_element() {
      var bindings = newBindings(elements("name", 7));
      assertThat(bindings.get("name")).isEqualTo(elements("name", 7));
    }

    @Test
    void getting_missing_element_throws_exception() {
      var bindings = newBindings();
      assertCall(() -> bindings.get("name")).throwsException(new NoSuchElementException("name"));
    }

    @Test
    void getMaybe_element() {
      var bindings = newBindings(elements("name", 7));
      assertThat(bindings.getMaybe("name")).isEqualTo(some(elements("name", 7)));
    }

    @Test
    void getMaybe_missing_element_returns_empty() {
      var bindings = newBindings();
      assertThat(bindings.getMaybe("name")).isEqualTo(none());
    }

    @Test
    void contains_present_element() {
      var bindings = newBindings(elements("name", 7));
      assertThat(bindings.contains("name")).isTrue();
    }

    @Test
    void contains_missing_element_returns_false() {
      var bindings = newBindings();
      assertThat(bindings.contains("name")).isFalse();
    }

    @Test
    void map() {
      var bindings = newBindings(elements("name", 7), elements("other", 5));
      var mapped = bindings.map(element -> element.value);
      assertThat(mapped.get("name")).isEqualTo(7);
    }

    @Test
    void asMap() {
      var first = elements("name", 7);
      var second = elements("other", 5);
      var bindings = newBindings(first, second);
      assertThat(bindings.toMap()).isEqualTo(Map.map("name", first, "other", second));
    }

    public abstract Bindings<Element> newBindings(Element... elements);
  }

  public abstract static class AbstractMutableBindingsTestSuite {
    public abstract MutableBindings<Integer> newBindings();

    @Test
    void add_return_null_if_binding_is_not_already_present() {
      var mutableBindings = newBindings();
      assertThat(mutableBindings.add("one", 1)).isEqualTo(null);
    }

    @Test
    void add_overwrites_previous_binding_if_present() {
      var mutableBindings = newBindings();
      mutableBindings.add("name", 1);
      mutableBindings.add("name", 2);
      assertThat(mutableBindings.get("name")).isEqualTo(2);
    }

    @Test
    void add_return_previous_binding_if_present() {
      var mutableBindings = newBindings();
      mutableBindings.add("name", 1);
      assertThat(mutableBindings.add("name", 2)).isEqualTo(1);
    }
  }

  public abstract static class AbstractScopedBindingsTestSuite {
    protected abstract Bindings<Element> newBindings(
        Bindings<Element> outerScope, Element... elements);

    protected abstract Bindings<Element> newBindingsWithInnerScopeWith(Element... elements);

    protected abstract Bindings<Element> newBindingsWithOuterScopeWith(Element... elements);

    @Nested
    class _bindings_tests_for_inner_scope extends AbstractBindingsTestSuite {
      @Override
      public Bindings<Element> newBindings(Element... elements) {
        return newBindingsWithInnerScopeWith(elements);
      }
    }

    @Nested
    class _bindings_tests_for_outer_scope extends AbstractBindingsTestSuite {
      @Override
      public Bindings<Element> newBindings(Element... elements) {
        return newBindingsWithOuterScopeWith(elements);
      }
    }

    @Nested
    class _to_map {
      @Test
      void contains_elements_from_outer_and_inner_scope() {
        var outer = immutableBindings(mapOfElems(elements("1", 1)));
        var bindings = newBindings(outer, elements("2", 2));
        assertThat(bindings.toMap()).isEqualTo(mapOfElems(elements("1", 1), elements("2", 2)));
      }

      @Test
      void does_not_contain_elements_from_outer_scope_overwritten_in_inner_scope() {
        var outer = immutableBindings(mapOfElems(elements("1", 1)));
        var bindings = newBindings(outer, elements("1", 11));
        assertThat(bindings.toMap()).isEqualTo(mapOfElems(elements("1", 11)));
      }
    }

    @Test
    void element_in_inner_bounds_shadows_element_from_outer_bounds() {
      var outer = immutableBindings(mapOfElems(elements("value-a", 7)));
      var shadowing = elements("value-a", 9);
      var inner = newBindings(outer, shadowing);
      assertThat(inner.get(shadowing.name())).isEqualTo(shadowing);
    }

    @Test
    void to_string() {
      var outer = immutableBindings(mapOfElems(elements("value-a", 7), elements("value-b", 8)));
      var inner = newBindings(outer, elements("value-c", 9));
      assertThat(inner.toString())
          .isEqualTo(
              """
            value-a -> Element[name=value-a, value=7]
            value-b -> Element[name=value-b, value=8]
              value-c -> Element[name=value-c, value=9]""");
    }
  }

  private static ImmutableBindings<Element> immutableBindingsWith(Element element) {
    return immutableBindings(map(element.name(), element));
  }

  private static Bindings<Element> mutableBindingsWith(Element... elements) {
    FlatMutableBindings<Element> bindings = mutableBindings();
    for (Element element : elements) {
      bindings.add(element.name(), element);
    }
    return bindings;
  }

  private static Bindings<Element> mutableBindingsWith(
      Bindings<Element> outerScopeBindings, Element... elements) {
    var mutableBindings = mutableBindings(outerScopeBindings);
    for (Element element : elements) {
      mutableBindings.add(element.name(), element);
    }
    return mutableBindings;
  }

  public static Map<String, Element> mapOfElems(Element... nameables) {
    return list(nameables).toMap(Element::name, e -> e);
  }

  public static Element elements(String name, int value) {
    return new Element(name, value);
  }

  protected static record Element(String name, Integer value) {}
}
