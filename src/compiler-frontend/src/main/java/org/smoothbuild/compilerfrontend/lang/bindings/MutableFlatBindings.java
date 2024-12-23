package org.smoothbuild.compilerfrontend.lang.bindings;

import java.util.HashMap;

/**
 * Immutable bindings with single scope.
 */
public final class MutableFlatBindings<E> extends FlatBindings<E> implements MutableBindings<E> {
  MutableFlatBindings() {
    super(new HashMap<>());
  }

  @Override
  public E add(String name, E elem) {
    return map.put(name, elem);
  }
}
