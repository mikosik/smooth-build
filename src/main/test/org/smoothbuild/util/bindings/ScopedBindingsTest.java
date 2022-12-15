package org.smoothbuild.util.bindings;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.bindings.AbstractBindingsTestSuite.elem;
import static org.smoothbuild.util.bindings.AbstractBindingsTestSuite.mapOfElems;
import static org.smoothbuild.util.bindings.ImmutableBindings.immutableBindings;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.util.bindings.AbstractBindingsTestSuite.Elem;

public class ScopedBindingsTest {
  @Nested
  class _elem_in_outer_scope extends AbstractBindingsTestSuite {
    @Override
    public Bindings<Elem> newBindings(Elem... elems) {
      var outer = immutableBindings(mapOfElems(elems));
      return new ScopedBindings<>(outer);
    }
  }

  @Nested
  class _elem_in_inner_scope extends AbstractBindingsTestSuite {
    @Override
    public Bindings<Elem> newBindings(Elem... elems) {
      return ScopedBindingsTest.newBindings(immutableBindings(), elems);
    }
  }

  @Test
  public void element_in_inner_bounds_shadows_element_from_outer_bounds() {
    var outer = immutableBindings(mapOfElems(elem("value-a", 7)));
    var shadowing = elem("value-a", 9);
    var inner = newBindings(outer, shadowing);
    assertThat(inner.get(shadowing.name()))
        .isEqualTo(shadowing);
  }

  @Test
  public void to_string() {
    var empty = ImmutableBindings.<Elem>immutableBindings();
    var inner = newBindings(empty, elem("value-a", 7), elem("value-b", 8));
    var outer = newBindings(inner, elem("value-c", 9));
    assertThat(outer.toString())
        .isEqualTo("""
              Elem[name=value-c, value=9]
                Elem[name=value-a, value=7]
                Elem[name=value-b, value=8]
                  <no bindings>""");
  }

  private static ScopedBindings<Elem> newBindings(Bindings<Elem> innerScope, Elem... elems) {
    var scopedBindings = new ScopedBindings<>(innerScope);
    for (Elem elem : elems) {
      scopedBindings.add(elem.name(), elem);
    }
    return scopedBindings;
  }
}
