package org.smoothbuild.util.bindings;

import static org.smoothbuild.util.bindings.AbstractBindingsTestSuite.mapOfElems;
import static org.smoothbuild.util.bindings.ImmutableBindings.immutableBindings;

import org.smoothbuild.util.bindings.AbstractBindingsTestSuite.Elem;

public class ImmutableBindingsTest extends AbstractScopedBindingsTestSuite {
  @Override
  protected Bindings<Elem> newMapBindings(ImmutableBindings<Elem> outerScope, Elem... elems) {
    return immutableBindings(outerScope, mapOfElems(elems));
  }
}
