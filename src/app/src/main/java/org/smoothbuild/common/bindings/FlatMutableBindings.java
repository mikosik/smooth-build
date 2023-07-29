package org.smoothbuild.common.bindings;

import java.util.HashMap;

/**
 * Immutable bindings with single scope.
 */
public final class FlatMutableBindings<E> extends FlatBindings<E> implements MutableBindings<E> {
  FlatMutableBindings() {
    super(new HashMap<>());
  }

  @Override
  public E add(String name, E elem) {
    return map.put(name, elem);
  }
}
