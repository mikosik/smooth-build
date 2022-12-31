package org.smoothbuild.util.bindings;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;
import static org.smoothbuild.util.collect.Maps.mapValues;

import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.smoothbuild.util.collect.Maps;

import com.google.common.collect.ImmutableMap;

public abstract class Bindings<E> {
  private final Bindings<? extends E> outerScopeBindings;

  public static <E> SingleScopeBindings<E> immutableBindings() {
    return new SingleScopeBindings<>(ImmutableMap.of());
  }

  public static <E> SingleScopeBindings<E> immutableBindings(
      Map<String, ? extends E> innerScopeMap) {
    return new SingleScopeBindings<>(innerScopeMap);
  }

  public static <E> ImmutableBindings<E> immutableBindings(
      ImmutableBindings<? extends E> outerScopeBindings, Map<String, ? extends E> innerScopeMap) {
    return new ImmutableBindings<>(outerScopeBindings, innerScopeMap);
  }

  public static <T> MutableBindings<T> mutableBindings() {
    return new MutableBindings<>(null);
  }

  public static <T> MutableBindings<T> mutableBindings(Bindings<? extends T> outerScopeBindings) {
    return new MutableBindings<>(outerScopeBindings);
  }

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

  public Map<String, E> innermostScopeMap() {
    return ImmutableMap.copyOf(innermostScopeMapImpl());
  }

  protected abstract Map<String, E> innermostScopeMapImpl();

  public Optional<E> getOptional(String name) {
    E element = innermostScopeMapImpl().get(name);
    if (element == null) {
      if (outerScopeBindings == null) {
        return Optional.empty();
      } else {
        return outerScopeBindings.getOptional(name).map(e -> e);
      }
    } else {
      return Optional.of(element);
    }
  }

  public <T> ImmutableBindings<T> map(Function<? super E, T> mapper) {
    var mappedInner = mapValues(innermostScopeMapImpl(), mapper);
    if (outerScopeBindings == null) {
      return immutableBindings(mappedInner);
    } else {
      var mappedOuter = outerScopeBindings.map(mapper);
      return immutableBindings(mappedOuter, mappedInner);
    }
  }

  public ImmutableMap<String, E> toMap() {
    return Maps.override(innermostScopeMap(), outerScopeBindings.toMap());
  }

  public static <E> SingleScopeBindings<E> override(
      Bindings<E> overriding,
      Bindings<E> overriden) {
    return immutableBindings(Maps.override(overriding.toMap(), overriden.toMap()));
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof Bindings<?> that
        && Objects.equals(this.outerScopeBindings, that.outerScopeBindings)
        && Objects.equals(this.innermostScopeMapImpl(), that.innermostScopeMapImpl());
  }

  @Override
  public int hashCode() {
    return Objects.hash(outerScopeBindings, innermostScopeMapImpl());
  }

  @Override
  public String toString() {
    var innerBindings = bindingsToString(innermostScopeMapImpl());
    if (outerScopeBindings == null) {
      return innerBindings;
    } else {
      return outerScopeBindings + "\n" + indent(innerBindings);
    }
  }
  public static <T> String bindingsToString(Map<String, T> bindings) {
    var string = joinToString(bindings.entrySet(), Bindings::bindingToString, "\n");
    return string.isEmpty() ? "<no bindings>" : string;
  }

  private static <T> String bindingToString(Entry<String, T> e) {
    return e.getKey() + " -> " + e.getValue();
  }
}
