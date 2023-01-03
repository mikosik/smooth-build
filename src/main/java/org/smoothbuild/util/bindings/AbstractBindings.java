package org.smoothbuild.util.bindings;

import static org.smoothbuild.util.bindings.Bindings.immutableBindings;

import java.util.NoSuchElementException;

public abstract sealed class AbstractBindings<E>
    implements Bindings<E>
    permits FlatBindings, ScopedBindings {

  @Override
  public E get(String name) {
    return getOptional(name)
        .orElseThrow(() -> new NoSuchElementException(name));
  }

  @Override
  public boolean contains(String name) {
    return getOptional(name).isPresent();
  }

  @Override
  public FlatImmutableBindings<E> toFlatImmutable() {
    return immutableBindings(asMap());
  }
}
