package org.smoothbuild.util.bindings;

import java.util.Optional;

import org.smoothbuild.util.collect.Named;

import com.google.common.collect.ImmutableMap;

public final class ImmutableBindings<E extends Named> extends Bindings<E> {
  private final ImmutableMap<String, E> bindings;

  protected ImmutableBindings(ImmutableMap<String, ? extends E> map) {
    // Cast is safe because ImmutableMap is immutable.
    @SuppressWarnings("unchecked")
    var castMap = (ImmutableMap<String, E>) map;
    this.bindings = castMap;
  }

  @Override
  public Optional<E> getOpt(String name) {
    return Optional.ofNullable(bindings.get(name));
  }

  public ImmutableMap<String, E> asMap() {
    return bindings;
  }

  @Override
  public String toString() {
    return prettyPrint(bindings);
  }
}
