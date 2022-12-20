package org.smoothbuild.util.bindings;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class ImmutableBindings<E> extends Bindings<E> {
  protected final ImmutableMap<String, E> innerScopeMap;

  public static <E> FlatBindings<E> immutableBindings() {
    return new FlatBindings<>(ImmutableMap.of());
  }

  public static <E> FlatBindings<E> immutableBindings(Map<String, ? extends E> innerScopeMap) {
    return new FlatBindings<>(innerScopeMap);
  }

  public static <E> ImmutableBindings<E> immutableBindings(
      ImmutableBindings<? extends E> outerScopeBindings, Map<String, ? extends E> innerScopeMap) {
    return new ImmutableBindings<>(outerScopeBindings, innerScopeMap);
  }

  protected ImmutableBindings(
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
