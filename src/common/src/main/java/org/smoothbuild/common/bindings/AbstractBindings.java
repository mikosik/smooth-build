package org.smoothbuild.common.bindings;

import static org.smoothbuild.common.bindings.Bindings.immutableBindings;

import java.util.NoSuchElementException;

public abstract sealed class AbstractBindings<E> implements Bindings<E>
    permits FlatBindings, ScopedBindings {

  @Override
  public E get(String name) {
    return getMaybe(name).getOrThrow(() -> new NoSuchElementException(name));
  }

  @Override
  public boolean contains(String name) {
    return getMaybe(name).isSome();
  }

  @Override
  public FlatImmutableBindings<E> toFlatImmutable() {
    return immutableBindings(toMap());
  }
}