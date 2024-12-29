package org.smoothbuild.compilerfrontend.lang.bindings;

import org.smoothbuild.common.collect.Map;

/**
 * Immutable bindings with single scope.
 */
public final class ImmutableFlatBindings<E> extends FlatBindings<E>
    implements ImmutableBindings<E> {
  ImmutableFlatBindings(Map<String, E> map) {
    super(map);
  }
}
