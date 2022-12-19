package org.smoothbuild.util.bindings;

import org.smoothbuild.util.bindings.AbstractBindingsTestSuite.Elem;

public class MutableBindingsTest extends AbstractScopedBindingsTestSuite {
  @Override
  protected Bindings<Elem> newMapBindings(Bindings<Elem> innerScope, Elem... elems) {
    var mutableBindings = new MutableBindings<>(innerScope);
    for (var elem : elems) {
      mutableBindings.add(elem.name(), elem);
    }
    return mutableBindings;
  }
}
