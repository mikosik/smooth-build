package org.smoothbuild.compilerfrontend.lang.bindings;

import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.function.Function1;

public sealed interface Bindings<E> permits AbstractBindings, ImmutableBindings, MutableBindings {
  public static <E> FlatImmutableBindings<E> immutableBindings() {
    return new FlatImmutableBindings<>(Map.map());
  }

  public static <E> FlatImmutableBindings<E> immutableBindings(Map<String, E> innerScopeMap) {
    return new FlatImmutableBindings<>(innerScopeMap);
  }

  public static <E> ScopedImmutableBindings<E> immutableBindings(
      ImmutableBindings<? extends E> outerScopeBindings, Map<String, ? extends E> innerScopeMap) {
    return immutableBindings(outerScopeBindings, immutableBindings(innerScopeMap));
  }

  public static <E> ScopedImmutableBindings<E> immutableBindings(
      ImmutableBindings<? extends E> outerScopeBindings,
      ImmutableBindings<? extends E> innerScopeBindings) {
    return new ScopedImmutableBindings<>(outerScopeBindings, innerScopeBindings);
  }

  public static <E> FlatMutableBindings<E> mutableBindings() {
    return new FlatMutableBindings<>();
  }

  public static <E> ScopedMutableBindings<E> mutableBindings(Bindings<E> outerScopeBindings) {
    return new ScopedMutableBindings<>(outerScopeBindings);
  }

  public boolean contains(String name);

  public E get(String name);

  public Maybe<E> getMaybe(String name);

  public <F, T extends Throwable> Bindings<F> map(Function1<? super E, F, T> mapper) throws T;

  public Map<String, E> toMap();

  public FlatImmutableBindings<E> toFlatImmutable();
}
