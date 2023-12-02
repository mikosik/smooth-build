package org.smoothbuild.common.bindings;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Function;
import org.smoothbuild.common.collect.Maybe;

public sealed interface Bindings<E> permits AbstractBindings, ImmutableBindings, MutableBindings {
  public static <E> FlatImmutableBindings<E> immutableBindings() {
    return new FlatImmutableBindings<>(ImmutableMap.of());
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

  public static <T> FlatMutableBindings<T> mutableBindings() {
    return new FlatMutableBindings<>();
  }

  public static <T> ScopedMutableBindings<T> mutableBindings(Bindings<T> outerScopeBindings) {
    return new ScopedMutableBindings<>(outerScopeBindings);
  }

  public boolean contains(String name);

  public E get(String name);

  public Maybe<E> getMaybe(String name);

  public <M> Bindings<M> map(Function<? super E, M> mapper);

  public ImmutableMap<String, E> toMap();

  public FlatImmutableBindings<E> toFlatImmutable();
}
