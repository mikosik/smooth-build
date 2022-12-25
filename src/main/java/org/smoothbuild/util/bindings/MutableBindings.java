package org.smoothbuild.util.bindings;

import java.util.HashMap;
import java.util.Map;

public class MutableBindings<E> extends Bindings<E> {
  private final HashMap<String, E> innerScopeMap;

  protected MutableBindings(Bindings<? extends E> outerScopeBindings) {
    super(outerScopeBindings);
    this.innerScopeMap = new HashMap<>();
  }

  @Override
  protected Map<String, E> innermostScopeMapImpl() {
    return innerScopeMap;
  }

  public E add(String name, E elem) {
    return innerScopeMap.put(name, elem);
  }
}
