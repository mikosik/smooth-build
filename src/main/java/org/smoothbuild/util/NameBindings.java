package org.smoothbuild.util;

import static java.util.stream.Collectors.joining;

import java.util.Map;
import java.util.NoSuchElementException;

import org.smoothbuild.util.collect.Nameable;

public class NameBindings<E extends Nameable> {
  private final NameBindings<? extends E> outerScopeBindings;
  private final Map<String, ? extends E> bindings;

  public NameBindings(Map<String, ? extends E> bindings) {
    this(null, bindings);
  }

  public NameBindings(NameBindings<? extends E> outerScopeBindings,
      Map<String, ? extends E> bindings) {
    this.outerScopeBindings = outerScopeBindings;
    this.bindings = bindings;
  }

  public boolean contains(String name) {
    return bindings.containsKey(name) || outerScopeContainsName(name);
  }

  private boolean outerScopeContainsName(String name) {
    return outerScopeBindings != null && outerScopeBindings.contains(name);
  }

  public E get(String name) {
    if (bindings.containsKey(name)) {
      return bindings.get(name);
    }
    if (outerScopeBindings != null) {
      return outerScopeBindings.get(name);
    }
    throw new NoSuchElementException(name);
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
