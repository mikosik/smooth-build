package org.smoothbuild.util;

import static java.util.stream.Collectors.joining;

import java.util.Map;
import java.util.NoSuchElementException;

import org.smoothbuild.util.collect.Nameable;

import com.google.common.collect.ImmutableMap;

public class Bindings<E extends Nameable> {
  private final Bindings<? extends E> outerScopeBindings;
  private final Map<String, ? extends E> bindings;

  public static <E extends Nameable> Bindings<E> bindings(ImmutableMap<String, E> map) {
    return new Bindings<>(null, map);
  }

  public MutableBindings<E> newInnerScope() {
    return new MutableBindings<>(this);
  }

  public Bindings<E> newInnerScope(ImmutableMap<String, ? extends E> map) {
    return new Bindings<>(this, map);
  }

  protected Bindings(Bindings<? extends E> outerScopeBindings, Map<String, ? extends E> map) {
    this.outerScopeBindings = outerScopeBindings;
    this.bindings = map;
  }

  public boolean contains(String name) {
    return bindings.containsKey(name) || outerScopeContainsName(name);
  }

  private boolean outerScopeContainsName(String name) {
    return outerScopeBindings != null && outerScopeBindings.contains(name);
  }

  public E get(String name) {
    E element = bindings.get(name);
    if (element != null) {
      return element;
    }
    if (outerScopeBindings != null) {
      return outerScopeBindings.get(name);
    }
    throw new NoSuchElementException(name);
  }

  public ImmutableMap<String, E> innerScopeBindings() {
    return ImmutableMap.copyOf(bindings);
  }

  @Override
  public String toString() {
    String outer = outerScopeBindings == null ? "" : outerScopeBindings + "\n";
    String inner = prettyPrint();
    return outer + inner;
  }

  private String prettyPrint() {
    return bindings.values().stream()
        .map(s -> indent() + s)
        .collect(joining("\n"));
  }

  private String indent() {
    return outerScopeBindings == null ? "" : outerScopeBindings.indent() + "  ";
  }
}
