package org.smoothbuild.util.bindings;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * Bindings with single scope.
 */
public class FlatBindings<E> extends ImmutableBindings<E> {
  protected FlatBindings(Map<String, ? extends E> innerScopeMap) {
    super(null, innerScopeMap);
  }

  @Override
  public ImmutableMap<String, E> toMap() {
    return innerScopeMap;
  }
}
