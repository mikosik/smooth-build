package org.smoothbuild.util.bindings;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.util.collect.Maps.mapValues;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

public abstract class AbstractBindings<E> implements Bindings<E>{
  @Override
  public E get(String name) {
    E element = getOrNull(name);
    if (element == null) {
      throw new NoSuchElementException(name);
    } else {
      return element;
    }
  }

  @Override
  public boolean contains(String name) {
    return getOrNull(name) != null;
  }

  @Override
  public abstract E getOrNull(String name);

  @Override
  public abstract Map<String, E> asMap();

  @Override
  public <T> AbstractBindings<T> map(Function<E, T> mapper) {
    return new AbstractBindings<>() {
      @Override
      public T getOrNull(String name) {
        E element = AbstractBindings.this.getOrNull(name);
        return element == null ? null : mapper.apply(element);
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
