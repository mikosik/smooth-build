package org.smoothbuild.util.bindings;

import java.util.Map;

public class FlatBindings<E> extends ImmutableBindings<E> {
  protected FlatBindings(Map<String, ? extends E> innerScopeMap) {
    super(null, innerScopeMap);
  }

  public Map<String, E> toMap() {
    return innerScopeMap;
  }
}
