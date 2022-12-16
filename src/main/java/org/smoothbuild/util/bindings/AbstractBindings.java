package org.smoothbuild.util.bindings;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.util.collect.Maps.mapValues;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractBindings<E> implements Bindings<E>{
  @Override
  public E get(String name) {
    return getOptional(name)
        .orElseThrow(() -> new NoSuchElementException(name));
  }

  @Override
  public boolean contains(String name) {
    return getOptional(name).isPresent();
  }

  @Override
  public abstract Optional<E> getOptional(String name);

  @Override
  public abstract Map<String, E> asMap();

  @Override
  public <T> AbstractBindings<T> map(Function<E, T> mapper) {
    return new AbstractBindings<>() {
      @Override
      public Optional<T> getOptional(String name) {
        return AbstractBindings.this.getOptional(name).map(mapper);
      }

      @Override
      public Map<String, T> asMap() {
        return mapValues(AbstractBindings.this.asMap(), mapper);
      }
    };
  }

  public static <T> String bindingsToString(Map<String, T> bindings) {
    var string = bindings.values().stream()
        .map(s -> "" + s)
        .collect(joining("\n"));
    return string.isEmpty() ? "<no bindings>" : string;
  }
}
