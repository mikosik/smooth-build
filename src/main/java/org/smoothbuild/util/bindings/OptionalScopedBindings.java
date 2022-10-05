package org.smoothbuild.util.bindings;

import static org.smoothbuild.util.bindings.ImmutableBindings.immutableBindings;
import static org.smoothbuild.util.collect.Maps.mapValues;

import java.util.Optional;

public class OptionalScopedBindings<T> extends ScopedBindings<Optional<T>> {
  public OptionalScopedBindings(Bindings<? extends Optional<T>> outerScopeBindings) {
    super(outerScopeBindings);
  }

  public ImmutableBindings<T> innerScopeBindings() {
    return immutableBindings(mapValues(innerScopeBindings, Optional::get));
  }
}
