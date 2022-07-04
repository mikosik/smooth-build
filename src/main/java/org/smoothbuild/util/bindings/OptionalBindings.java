package org.smoothbuild.util.bindings;

import static java.util.Objects.requireNonNullElseGet;
import static org.smoothbuild.util.bindings.ImmutableBindings.immutableBindings;
import static org.smoothbuild.util.collect.Maps.mapValues;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class OptionalBindings<E> {
  protected final Map<String, Bound<? extends E>> bindings;

  public static <T> OptionalBindings<T> newOptionalBindings(
      ImmutableBindings<? extends T> outerScope) {
    return new OptionalBindings<>() {
      @Override
      protected Bound<? extends T> getFromOuterScope(String name) {
        var result = outerScope.getOpt(name);
        if (result.isPresent()) {
          return new Bound<>(result);
        } else {
          return new Bound<>();
        }
      }
    };
  }

  public static <T> OptionalBindings<T> newOptionalBindings(
      OptionalBindings<? extends T> outerScope) {
    return new OptionalBindings<>() {
      @Override
      protected Bound<? extends T> getFromOuterScope(String name) {
        return outerScope.get(name);
      }
    };
  }

  private OptionalBindings() {
    this.bindings = new HashMap<>();
  }

  public void add(String name, Optional<? extends E> bound) {
    bindings.putIfAbsent(name, new Bound<>(bound));
  }

  public Bound<? extends E> get(String name) {
    return requireNonNullElseGet(bindings.get(name), () -> getFromOuterScope(name));
  }

  protected abstract Bound<? extends E> getFromOuterScope(String name);

  public ImmutableBindings<E> innerScopeBindings() {
    return immutableBindings(mapValues(bindings, b -> b.value().get()));
  }
}
