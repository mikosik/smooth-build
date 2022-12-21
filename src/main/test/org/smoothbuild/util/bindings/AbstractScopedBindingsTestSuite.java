package org.smoothbuild.util.bindings;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.bindings.AbstractBindingsTestSuite.elem;
import static org.smoothbuild.util.bindings.AbstractBindingsTestSuite.mapOfElems;
import static org.smoothbuild.util.bindings.ImmutableBindings.immutableBindings;

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
            Elem[name=value-a, value=7]
            Elem[name=value-b, value=8]
              Elem[name=value-c, value=9]""");
  }
}
