package org.smoothbuild.util.bindings;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.smoothbuild.util.collect.Named;

import com.google.common.collect.ImmutableMap;

public final class MutableBindings<E extends Named> extends Bindings<E> {
  private final Bindings<E> outerScopeBindings;
  private final Map<String, E> bindings;

  public MutableBindings(Bindings<E> outerScopeBindings) {
    this.outerScopeBindings = outerScopeBindings;
    this.bindings = new HashMap<>();
  }

  public MutableBindings<E> add(E value) {
    bindings.put(value.nameO().get(), value);
    return this;
  }

  public MutableBindings<E> addAll(Map<String, ? extends E> map) {
    bindings.putAll(map);
    return this;
  }

  @Override
  public Optional<E> getOpt(String name) {
    return Optional.ofNullable(bindings.get(name))
        .or(() -> outerScopeBindings.getOpt(name));
  }

  public ImmutableBindings<E> innerScope() {
    return immutableBindings(ImmutableMap.copyOf(bindings));
  }

  @Override
  public String toString() {
    return outerScopeBindings + "\n" + prettyPrint(bindings);
  }

  @Override
  protected String indent() {
    return outerScopeBindings.indent() + "  ";
  }
}
