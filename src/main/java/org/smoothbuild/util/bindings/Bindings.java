package org.smoothbuild.util.bindings;

import static java.util.stream.Collectors.joining;

import java.util.Map;
import java.util.Optional;

import org.smoothbuild.util.collect.Nameable;

import com.google.common.collect.ImmutableMap;

public sealed abstract class Bindings<E extends Nameable>
    permits MutableBindings, ImmutableBindings {
  public static <E extends Nameable> ImmutableBindings<E> immutableBindings() {
    return immutableBindings(ImmutableMap.of());
  }

  public static <E extends Nameable> ImmutableBindings<E> immutableBindings(
      ImmutableMap<String, ? extends E> map) {
    return new ImmutableBindings<>(map);
  }

  public MutableBindings<E> newMutableScope() {
    return new MutableBindings<>(this);
  }

  public boolean contains(String name) {
    return getOpt(name).isPresent();
  }

  public E get(String name) {
    return getOpt(name).get();
  }

  public abstract Optional<E> getOpt(String name);

  protected String prettyPrint(Map<String, E> bindings) {
    return bindings.values().stream()
        .map(s -> indent() + s)
        .collect(joining("\n"));
  }

  protected String indent() {
    return "";
  }
}
