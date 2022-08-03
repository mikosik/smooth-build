package org.smoothbuild.util.bindings;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.util.collect.Maps.mapValues;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

public abstract class Bindings<E> {
  public E get(String name) {
    E element = getOrNull(name);
    if (element == null) {
      throw new NoSuchElementException(name);
    } else {
      return element;
    }
  }

  public boolean contains(String name) {
    return getOrNull(name) != null;
  }

  public abstract E getOrNull(String name);

  public abstract Map<String, E> asMap();

  public <T> Bindings<T> map(Function<E, T> mapper) {
    return new Bindings<>() {
      @Override
      public T getOrNull(String name) {
        E element = Bindings.this.getOrNull(name);
        return element == null ? null : mapper.apply(element);
      }

      @Override
      public Map<String, T> asMap() {
        return mapValues(Bindings.this.asMap(), mapper);
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
