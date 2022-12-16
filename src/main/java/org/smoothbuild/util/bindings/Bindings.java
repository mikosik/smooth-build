package org.smoothbuild.util.bindings;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public interface Bindings<E> {
  public E get(String name);

  public boolean contains(String name);

  public Optional<E> getOptional(String name);

  public Map<String, E> asMap();

  public <T> Bindings<T> map(Function<E, T> mapper);
}
