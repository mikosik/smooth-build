package org.smoothbuild.util.bindings;

import java.util.Map;

public class SingleScopeBindings<E> extends ImmutableBindings<E> {
  protected SingleScopeBindings(Map<String, ? extends E> innerScopeMap) {
    super(null, innerScopeMap);
  }

  public Map<String, E> toMap() {
    return innerScopeMap;
  }
}
