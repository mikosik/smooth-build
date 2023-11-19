package org.smoothbuild.common.bindings;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;

/**
 * Immutable bindings with single scope.
 */
public final class FlatImmutableBindings<E> extends FlatBindings<E>
    implements ImmutableBindings<E> {
  FlatImmutableBindings(Map<String, E> map) {
    super(ImmutableMap.copyOf(map));
  }

  @Override
  public Optional<E> getOptional(String name) {
    return Optional.ofNullable(map.get(name));
  }
}
