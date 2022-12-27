package org.smoothbuild.util.bindings;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class SingleScopeBindings<E> extends ImmutableBindings<E> {
  protected SingleScopeBindings(Map<String, ? extends E> innerScopeMap) {
    super(null, innerScopeMap);
  }

  public ImmutableMap<String, E> toMap() {
    return innerScopeMap;
  }
}
