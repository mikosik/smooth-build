package org.smoothbuild.compilerfrontend.lang.bindings;

import static org.smoothbuild.compilerfrontend.lang.bindings.Bindings.immutableBindings;

public abstract sealed class AbstractBindings<E> implements Bindings<E>
    permits FlatBindings, ScopedBindings {
  @Override
  public ImmutableFlatBindings<E> toFlatImmutable() {
    return immutableBindings(toMap());
  }
}
