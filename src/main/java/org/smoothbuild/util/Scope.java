package org.smoothbuild.util;

import static java.util.stream.Collectors.joining;

import java.util.NoSuchElementException;

import org.smoothbuild.util.collect.Nameable;
import org.smoothbuild.util.collect.NamedList;

public class Scope<E extends Nameable> {
  private final Scope<? extends E> outerScope;
  private final NamedList<? extends E> bindings;

  public Scope(NamedList<? extends E> bindings) {
    this(null, bindings);
  }

  public Scope(Scope<? extends E> outerScope, NamedList<? extends E> bindings) {
    this.outerScope = outerScope;
    this.bindings = bindings;
  }

  public boolean contains(String name) {
    return bindings.containsName(name) || (outerScope != null && outerScope.contains(name));
  }

  public E get(String name) {
    if (bindings.containsName(name)) {
      return bindings.get(name);
    }
    if (outerScope != null) {
      return outerScope.get(name);
    }
    throw new NoSuchElementException(name);
  }

  public Scope<? extends E> outerScope() {
    if (outerScope == null) {
      throw new IllegalStateException("This is top level scope. It doesn't have outer scope.");
    }
    return outerScope;
  }

  @Override
  public String toString() {
    String outer = outerScope == null ? "" : outerScope + "\n";
    String inner = prettyPrint();
    return outer + inner;
  }

  private String prettyPrint() {
    return bindings.stream()
        .map(s -> indent() + s)
        .collect(joining("\n"));
  }

  private String indent() {
    return outerScope == null ? "" : outerScope.indent() + "  ";
  }
}
