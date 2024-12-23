package org.smoothbuild.compilerfrontend.lang.bindings;

import static org.smoothbuild.common.collect.Maybe.maybe;

import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Maybe;

/**
 * Immutable bindings with single scope.
 */
public final class FlatImmutableBindings<E> extends FlatBindings<E>
    implements ImmutableBindings<E> {
  FlatImmutableBindings(Map<String, E> map) {
    super(map);
  }

  @Override
  public Maybe<E> getMaybe(String name) {
    return maybe(map.get(name));
  }
}
