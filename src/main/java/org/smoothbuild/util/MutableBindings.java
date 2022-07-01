package org.smoothbuild.util;

import java.util.HashMap;
import java.util.Map;

import org.smoothbuild.util.collect.Nameable;

public class MutableBindings<E extends Nameable> extends Bindings<E> {
  private final Map<String, E> bindings;

  public MutableBindings(Bindings<? extends E> outerScopeBindings) {
    this(outerScopeBindings, new HashMap<>());
  }

  public MutableBindings(Bindings<? extends E> outerScopeBindings, Map<String, E> bindings) {
    super(outerScopeBindings, bindings);
    this.bindings = bindings;
  }

  public void add(E value) {
    bindings.put(value.nameO().get(), value);
  }

  public void addAll(Map<String, ? extends E> map2) {
    bindings.putAll(map2);
  }
}
