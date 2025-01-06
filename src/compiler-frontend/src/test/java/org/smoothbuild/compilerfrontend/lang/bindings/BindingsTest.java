package org.smoothbuild.compilerfrontend.lang.bindings;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Result.err;
import static org.smoothbuild.common.collect.Result.ok;
import static org.smoothbuild.compilerfrontend.lang.bindings.Bindings.immutableBindings;
import static org.smoothbuild.compilerfrontend.lang.bindings.Bindings.mutableBindings;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;
import static org.smoothbuild.compilerfrontend.lang.name.Name.referenceableName;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.base.HasName;
import org.smoothbuild.compilerfrontend.lang.name.Name;

public class BindingsTest {
  @Nested
  class _immutable_flat_bindings {
    @Nested
    class _bindings_tests extends AbstractBindingsTestSuite {
      @Override
      public Bindings<Element> newBindings(Element... elements) {
        return immutableBindings(list(elements));
      }
    }
  }

  @Nested
  class _mutable_flat_bindings {
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
  class _immutable_scoped_bindings {
    @Nested
    class _scoped_bindings_tests extends AbstractScopedBindingsTestSuite {
      @Override
      protected Bindings<Element> newBindings(Bindings<Element> outerScope, Element... elements) {
        return mutableBindingsWith(outerScope, elements);
      }

      @Override
      protected Bindings<Element> newBindingsWithInnerScopeWith(Element... elements) {
        return immutableBindings(immutableBindings(), immutableBindings(list(elements)));
      }

      @Override
      protected Bindings<Element> newBindingsWithOuterScopeWith(Element... elements) {
        return immutableBindings(immutableBindings(list(elements)), immutableBindings());
      }
    }
  }

  @Nested
  class _mutable_scoped_bindings {
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
        return mutableBindingsWith(immutableBindings(list(elements)));
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
    var elem1 = element("name1", 1);
    var elem2 = element("name2", 2);

    var equalsTester = new EqualsTester();
    // flat bindings with no elements
    equalsTester.addEqualityGroup(
        immutableBindings(), immutableBindings(list()), mutableBindings());

    // flat bindings with elem1
    equalsTester.addEqualityGroup(immutableBindings(list(elem1)), mutableBindingsWith(elem1));

    // flat bindings with elem2
    equalsTester.addEqualityGroup(immutableBindings(list(elem2)), mutableBindingsWith(elem2));

    // scoped bindings with elem1 in outer scope
    equalsTester.addEqualityGroup(
        immutableBindings(immutableBindings(list(elem1)), immutableBindings()),
        mutableBindings(immutableBindings(list(elem1))));

    // scoped bindings with elem1 in inner scope
    equalsTester.addEqualityGroup(
        immutableBindings(immutableBindings(), immutableBindings(list(elem1))),
        mutableBindingsWith(immutableBindings(), elem1));

    // scoped bindings with elem1 in outer and inner scope
    equalsTester.addEqualityGroup(
        immutableBindings(immutableBindings(list(elem1)), immutableBindings(list(elem1))),
        mutableBindingsWith(immutableBindings(list(elem1)), elem1));

    // element-1 in outer scope and element-2 in inner scope
    equalsTester.addEqualityGroup(
        immutableBindings(immutableBindings(list(elem1)), immutableBindings(list(elem2))),
        mutableBindingsWith(immutableBindings(list(elem1)), elem2));

    equalsTester.testEquals();
  }

  public abstract static class AbstractBindingsTestSuite {
    @Test
    void find_element() {
      var bindings = newBindings(element("name", 7));
      assertThat(bindings.find(name("name"))).isEqualTo(ok(element("name", 7)));
    }

    @Test
    void find_missing_element_returns_error() {
      var bindings = newBindings();
      assertThat(bindings.find(name("name"))).isEqualTo(err("Cannot resolve `name`."));
    }

    @Test
    void find_missing_nested_element_returns_error() {
      var bindings = newBindings(element("name", 7));
      assertThat(bindings.find(fqn("name:nested"))).isEqualTo(err("Cannot resolve `name:nested`."));
    }

    @Test
    void get_element() {
      var bindings = newBindings(element("name", 7));
      assertThat(bindings.get(name("name"))).isEqualTo(element("name", 7));
    }

    @Test
    void get_missing_element_returns_null() {
      var bindings = newBindings();
      assertThat(bindings.get(name("name"))).isNull();
    }

    @Test
    void asMap() {
      var first = element("name", 7);
      var second = element("other", 5);
      var bindings = newBindings(first, second);
      assertThat(bindings.toMap()).isEqualTo(Map.map(name("name"), first, name("other"), second));
    }

    public abstract Bindings<Element> newBindings(Element... elements);
  }

  public abstract static class AbstractMutableBindingsTestSuite {
    public abstract MutableBindings<Integer> newBindings();

    @Test
    void add_return_null_if_binding_is_not_already_present() {
      var mutableBindings = newBindings();
      assertThat(mutableBindings.add(name("one"), 1)).isEqualTo(null);
    }

    @Test
    void add_overwrites_previous_binding_if_present() {
      var mutableBindings = newBindings();
      mutableBindings.add(name("name"), 1);
      mutableBindings.add(name("name"), 2);
      assertThat(mutableBindings.find(name("name"))).isEqualTo(ok(2));
    }

    @Test
    void add_return_previous_binding_if_present() {
      var mutableBindings = newBindings();
      mutableBindings.add(name("name"), 1);
      assertThat(mutableBindings.add(name("name"), 2)).isEqualTo(1);
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
        var outer = immutableBindings(list(element("n1", 1)));
        var bindings = newBindings(outer, element("n2", 2));
        assertThat(bindings.toMap()).isEqualTo(mapOfElems(element("n1", 1), element("n2", 2)));
      }

      @Test
      void does_not_contain_elements_from_outer_scope_overwritten_in_inner_scope() {
        var outer = immutableBindings(list(element("n1", 1)));
        var bindings = newBindings(outer, element("n1", 11));
        assertThat(bindings.toMap()).isEqualTo(mapOfElems(element("n1", 11)));
      }
    }

    @Test
    void element_in_inner_bounds_shadows_element_from_outer_bounds() {
      var outer = immutableBindings(list(element("valueA", 7)));
      var shadowing = element("valueA", 9);
      var inner = newBindings(outer, shadowing);
      assertThat(inner.find(shadowing.name())).isEqualTo(ok(shadowing));
    }

    @Test
    void to_string() {
      var outer = immutableBindings(list(element("valueA", 7), element("valueB", 8)));
      var inner = newBindings(outer, element("valueC", 9));
      assertThat(inner.toString())
          .isEqualTo(
              """
            valueA -> Element[name=valueA, value=7]
            valueB -> Element[name=valueB, value=8]
              valueC -> Element[name=valueC, value=9]""");
    }
  }

  private static Bindings<Element> mutableBindingsWith(Element... elements) {
    MutableFlatBindings<Element> bindings = mutableBindings();
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

  public static Map<Name, Element> mapOfElems(Element... nameables) {
    return list(nameables).toMap(Element::name, e -> e);
  }

  public static Element element(String name, int value) {
    return new Element(name, value);
  }

  public static record Element(Name name, Integer value) implements HasName {
    public Element(String name, Integer value) {
      this(BindingsTest.name(name), value);
    }
  }

  private static Name name(String name) {
    return referenceableName(name);
  }
}
