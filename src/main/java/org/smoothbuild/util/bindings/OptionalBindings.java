package org.smoothbuild.util.bindings;

import static org.smoothbuild.util.bindings.Bindings.immutableBindings;
import static org.smoothbuild.util.collect.Maps.mapEntries;

import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;

public final class OptionalBindings<E> extends ScopedMutableBindings<Optional<E>> {
  public OptionalBindings(Bindings<? extends Optional<? extends E>> outerScopeBindings) {
    // Cast is safe here as outerScopeBindings won't be modified.
    super(unsafeCast(outerScopeBindings));
  }

  private static <T> Bindings<? extends Optional<T>> unsafeCast(
      Bindings<? extends Optional<? extends T>> bindings) {
    @SuppressWarnings("unchecked")
    Bindings<? extends Optional<T>> castBindings = (Bindings<? extends Optional<T>>) bindings;
    return castBindings;
  }

  public FlatImmutableBindings<E> innerScopeBindingsReduced() {
    return immutableBindings(mapEntries(
        innerMostScopeBindings().asMap(), OptionalBindings::reduceOptionals));
  }

  private static <T> Entry<String, T> reduceOptionals(Entry<String, ? extends Optional<T>> e) {
    return Map.entry(
        e.getKey(),
        (e.getValue()).orElseThrow(() -> newNoSuchElementException(e.getKey())));
  }

  private static NoSuchElementException newNoSuchElementException(String name) {
    return new NoSuchElementException("Nothing bound for name `" + name + "`.");
  }
}
