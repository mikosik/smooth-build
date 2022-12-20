package org.smoothbuild.util.bindings;

import static com.google.common.collect.Streams.stream;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.util.bindings.ImmutableBindings.immutableBindings;
import static org.smoothbuild.util.collect.Lists.joinToString;
import static org.smoothbuild.util.collect.Maps.mapValues;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.base.Splitter;

public abstract class Bindings<E> {
  private final Bindings<? extends E> outerScopeBindings;

  protected Bindings(Bindings<? extends E> outerScopeBindings) {
    this.outerScopeBindings = outerScopeBindings;
  }

  public E get(String name) {
    return getOptional(name)
        .orElseThrow(() -> new NoSuchElementException(name));
  }

  public boolean contains(String name) {
    return getOptional(name).isPresent();
  }

  protected abstract Map<String, E> innerScopeMap();

  public Optional<E> getOptional(String name) {
    E element = innerScopeMap().get(name);
    if (element == null) {
      if (outerScopeBindings == null) {
        return Optional.empty();
      } else {
        @SuppressWarnings("unchecked") // safe because Optional is immutable
        Optional<E> cast = (Optional<E>) outerScopeBindings.getOptional(name);
        return cast;
      }
    } else {
      return Optional.of(element);
    }
  }

  public <T> ImmutableBindings<T> map(Function<? super E, T> mapper) {
    var mappedOuter = outerScopeBindings == null ? null : outerScopeBindings.map(mapper);
    var mappedInner = mapValues(innerScopeMap(), mapper);
    return immutableBindings(mappedOuter, mappedInner);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof Bindings<?> that
        && Objects.equals(this.outerScopeBindings, that.outerScopeBindings)
        && Objects.equals(this.innerScopeMap(), that.innerScopeMap());
  }

  @Override
  public int hashCode() {
    return Objects.hash(outerScopeBindings, innerScopeMap());
  }

  @Override
  public String toString() {
    var innerBindings = bindingsToString(innerScopeMap());
    if (outerScopeBindings == null) {
      return innerBindings;
    } else {
      return innerBindings
          + "\n"
          + indent(outerScopeBindings.toString());
    }
  }
  public static <T> String bindingsToString(Map<String, T> bindings) {
    var string = joinToString(bindings.values(), "\n");
    return string.isEmpty() ? "<no bindings>" : string;
  }

  private String indent(String text) {
    var lines = Splitter.onPattern("\r?\n").split(text);
    return stream(lines)
        .map(l -> "  " + l)
        .collect(joining("\n"));
  }
}
