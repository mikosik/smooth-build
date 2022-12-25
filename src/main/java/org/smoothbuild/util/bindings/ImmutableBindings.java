package org.smoothbuild.util.bindings;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class ImmutableBindings<E> extends Bindings<E> {
  protected final ImmutableMap<String, E> innerScopeMap;

  protected ImmutableBindings(
      Bindings<? extends E> outerScopeBindings,
      Map<String, ? extends E> innerScopeMap) {
    super(outerScopeBindings);
    this.innerScopeMap = ImmutableMap.copyOf(innerScopeMap);
  }

  @Override
  protected Map<String, E> innermostScopeMapImpl() {
    return innerScopeMap;
  }
}
