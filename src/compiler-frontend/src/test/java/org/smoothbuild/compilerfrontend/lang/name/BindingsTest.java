package org.smoothbuild.compilerfrontend.lang.name;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Result.err;
import static org.smoothbuild.common.collect.Result.ok;
import static org.smoothbuild.compilerfrontend.lang.name.Bindings.bindings;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;
import static org.smoothbuild.compilerfrontend.lang.name.Name.referenceableName;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.base.HasName;

public class BindingsTest {
  @Nested
  class _scoped_bindings_tests {
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
        var outer = bindings(element("n1", 1));
        var bindings = newBindings(outer, element("n2", 2));
        assertThat(bindings.toMap()).isEqualTo(mapOfElems(element("n1", 1), element("n2", 2)));
      }

      @Test
      void does_not_contain_elements_from_outer_scope_overwritten_in_inner_scope() {
        var outer = bindings(element("n1", 1));
        var bindings = newBindings(outer, element("n1", 11));
        assertThat(bindings.toMap()).isEqualTo(mapOfElems(element("n1", 11)));
      }
    }

    @Test
    void element_in_inner_bounds_shadows_element_from_outer_bounds() {
      var outer = bindings(element("valueA", 7));
      var shadowing = element("valueA", 9);
      var inner = newBindings(outer, shadowing);
      assertThat(inner.find(shadowing.name())).isEqualTo(ok(shadowing));
    }

    @Test
    void to_string() {
      var outer = bindings(element("valueA", 7), element("valueB", 8));
      var inner = newBindings(outer, element("valueC", 9));
      assertThat(inner.toString())
          .isEqualTo(
              """
            valueA -> Element[name=valueA, value=7]
            valueB -> Element[name=valueB, value=8]
              valueC -> Element[name=valueC, value=9]""");
    }

    private Bindings<Element> newBindings(Bindings<Element> outerScope, Element... elements) {
      return bindings(outerScope, mapOfElems(elements));
    }

    private Bindings<Element> newBindingsWithInnerScopeWith(Element... elements) {
      return bindings(bindings(), mapOfElems(elements));
    }

    private Bindings<Element> newBindingsWithOuterScopeWith(Element... elements) {
      return bindings(bindings(elements), mapOfElems());
    }
  }

  @Test
  void equals_and_hashcode() {
    var elem1 = element("name1", 1);
    var elem2 = element("name2", 2);

    var equalsTester = new EqualsTester();
    // bindings with no elements
    equalsTester.addEqualityGroup(bindings(), bindings());

    // bindings with elem1
    equalsTester.addEqualityGroup(bindings(elem1), bindings(elem1));

    // bindings with elem2
    equalsTester.addEqualityGroup(bindings(elem2), bindings(elem2));

    // bindings with elem1 in outer scope
    equalsTester.addEqualityGroup(
        bindings(bindings(elem1), mapOfElems()), bindings(bindings(elem1), mapOfElems()));

    // bindings with elem1 in inner scope
    equalsTester.addEqualityGroup(
        bindings(bindings(), mapOfElems(elem1)), bindings(bindings(), mapOfElems(elem1)));

    // bindings with elem1 in outer and inner scope
    equalsTester.addEqualityGroup(
        bindings(bindings(elem1), mapOfElems(elem1)), bindings(bindings(elem1), mapOfElems(elem1)));

    // element-1 in outer scope and element-2 in inner scope
    equalsTester.addEqualityGroup(
        bindings(bindings(elem1), mapOfElems(elem2)), bindings(bindings(elem1), mapOfElems(elem2)));

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
