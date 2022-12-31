package org.smoothbuild.util.bindings;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.bindings.AbstractBindingsTestSuite.elem;
import static org.smoothbuild.util.bindings.AbstractBindingsTestSuite.mapOfElems;
import static org.smoothbuild.util.bindings.Bindings.immutableBindings;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.util.bindings.AbstractBindingsTestSuite.Elem;

public abstract class AbstractScopedBindingsTestSuite {
  protected abstract Bindings<Elem> newMapBindings(
      ImmutableBindings<Elem> outerScope, Elem... elems);

  @Nested
  class _elem_in_outer_scope extends AbstractBindingsTestSuite {
    @Override
    public Bindings<Elem> newBindings(Elem... elems) {
      var outer = immutableBindings(mapOfElems(elems));
      return newMapBindings(outer);
    }
  }

  @Nested
  class _elem_in_inner_scope extends AbstractBindingsTestSuite {
    @Override
    public Bindings<Elem> newBindings(Elem... elems) {
      return newMapBindings(immutableBindings(), elems);
    }

    @Test
    public void innermost_scope_map() {
      var outer = immutableBindings(mapOfElems(elem("1", 1)));
      var bindings = newMapBindings(outer, elem("2", 2));
      assertThat(bindings.innermostScopeMap())
          .isEqualTo(mapOfElems(elem("2", 2)));
    }
  }

  @Nested
  class _to_map {
    @Test
    public void returns_map_containing_elements_from_outer_and_inner_scope() {
      var outer = immutableBindings(mapOfElems(elem("1", 1)));
      var bindings = newMapBindings(outer, elem("2", 2));
      assertThat(bindings.toMap())
          .isEqualTo(mapOfElems(elem("1", 1), elem("2", 2)));
    }

    @Test
    public void returned_map_does_not_contain_elements_from_outer_scope_overwritten_in_inner_scope() {
      var outer = immutableBindings(mapOfElems(elem("1", 1)));
      var bindings = newMapBindings(outer, elem("1", 11));
      assertThat(bindings.toMap())
          .isEqualTo(mapOfElems(elem("1", 11)));
    }
  }

  @Test
  public void element_in_inner_bounds_shadows_element_from_outer_bounds() {
    var outer = immutableBindings(mapOfElems(elem("value-a", 7)));
    var shadowing = elem("value-a", 9);
    var inner = newMapBindings(outer, shadowing);
    assertThat(inner.get(shadowing.name()))
        .isEqualTo(shadowing);
  }

  @Test
  public void to_string() {
    var outer = immutableBindings(mapOfElems(elem("value-a", 7), elem("value-b", 8)));
    var inner = newMapBindings(outer, elem("value-c", 9));
    assertThat(inner.toString())
        .isEqualTo("""
            value-a -> Elem[name=value-a, value=7]
            value-b -> Elem[name=value-b, value=8]
              value-c -> Elem[name=value-c, value=9]""");
  }
}
