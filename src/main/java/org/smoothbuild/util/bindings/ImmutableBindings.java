package org.smoothbuild.util.bindings;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public final class ImmutableBindings<E> extends Bindings<E> {
  private final ImmutableMap<String, E> innerScopeMap;

  public static <E> ImmutableBindings<E> immutableBindings() {
    return immutableBindings(ImmutableMap.of());
  }

  public static <E> ImmutableBindings<E> immutableBindings(Map<String, ? extends E> innerScopeMap) {
    return new ImmutableBindings<>(null, innerScopeMap);
  }

  public static <E> ImmutableBindings<E> immutableBindings(
      Bindings<? extends E> outerScopeBindings, Map<String, ? extends E> innerScopeMap) {
    return new ImmutableBindings<>(outerScopeBindings, innerScopeMap);
  }

  private ImmutableBindings(
      Bindings<? extends E> outerScopeBindings,
      Map<String, ? extends E> innerScopeMap) {
    super(outerScopeBindings);
    this.innerScopeMap = ImmutableMap.copyOf(innerScopeMap);
  }

  @Override
  protected Map<String, E> innerScopeMap() {
    return innerScopeMap;
  }
}
