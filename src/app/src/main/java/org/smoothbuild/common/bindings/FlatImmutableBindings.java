package org.smoothbuild.common.bindings;

import static org.smoothbuild.common.collect.Maybe.maybe;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.smoothbuild.common.collect.Maybe;

/**
 * Immutable bindings with single scope.
 */
public final class FlatImmutableBindings<E> extends FlatBindings<E>
    implements ImmutableBindings<E> {
  FlatImmutableBindings(Map<String, E> map) {
    super(ImmutableMap.copyOf(map));
  }

  @Override
  public Maybe<E> getMaybe(String name) {
    return maybe(map.get(name));
  }
}
