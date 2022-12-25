package org.smoothbuild.util.bindings;

import static org.smoothbuild.util.bindings.Bindings.mutableBindings;

import org.smoothbuild.util.bindings.AbstractBindingsTestSuite.Elem;

public class MutableBindingsTest extends AbstractScopedBindingsTestSuite {
  @Override
  protected Bindings<Elem> newMapBindings(ImmutableBindings<Elem> outerScope, Elem... elems) {
    var mutableBindings = mutableBindings(outerScope);
    for (var elem : elems) {
      mutableBindings.add(elem.name(), elem);
    }
    return mutableBindings;
  }
}
