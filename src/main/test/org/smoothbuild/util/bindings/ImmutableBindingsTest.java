package org.smoothbuild.util.bindings;

import static org.smoothbuild.util.bindings.AbstractBindingsTestSuite.mapOfElems;

import org.smoothbuild.util.bindings.AbstractBindingsTestSuite.Elem;

public class ImmutableBindingsTest extends AbstractScopedBindingsTestSuite {
  @Override
  protected Bindings<Elem> newMapBindings(Bindings<Elem> innerScope, Elem... elems) {
    return ImmutableBindings.immutableBindings(innerScope, mapOfElems(elems));
  }
}
