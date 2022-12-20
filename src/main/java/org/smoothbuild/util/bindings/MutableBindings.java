package org.smoothbuild.util.bindings;

import java.util.HashMap;
import java.util.Map;

public class MutableBindings<E> extends Bindings<E> {
  private final HashMap<String, E> innerScopeMap;

  public MutableBindings() {
    this(null);
  }

  public MutableBindings(Bindings<? extends E> outerScopeBindings) {
    super(outerScopeBindings);
    this.innerScopeMap = new HashMap<>();
  }

  @Override
  protected Map<String, E> innerScopeMap() {
    return innerScopeMap;
  }

  public E add(String name, E elem) {
    return innerScopeMap.put(name, elem);
  }
}
