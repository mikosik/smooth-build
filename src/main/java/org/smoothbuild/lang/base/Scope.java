package org.smoothbuild.lang.base;

import static java.util.stream.Collectors.joining;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

public class Scope<E> {
  private final Scope<E> outerScope;
  private final Map<String, E> bindings = new HashMap<>();

  public static <E> Scope<E> scope() {
    return new Scope<E>(null);
  }

  public static <E> Scope<E> scope(Scope<E> outerScope) {
    return new Scope<>(outerScope);
  }

  public Scope(Scope<E> outerScope) {
    this.outerScope = outerScope;
  }

  public boolean contains(String name) {
    return bindings.containsKey(name) || (outerScope != null && outerScope.contains(name));
  }

  public E get(String name) {
    if (bindings.containsKey(name)) {
      return bindings.get(name);
    }
    if (outerScope != null) {
      return outerScope.get(name);
    }
    throw new NoSuchElementException(name);
  }

  public void add(String name, E element) {
    if (bindings.containsKey(name)) {
      throw new IllegalStateException("Name " + name + " is already bound in current scope.");
    }
    addOrReplace(name, element);
  }

  public void addOrReplace(String name, E element) {
    bindings.put(name, element);
  }

  public Scope<E> outerScope() {
    if (outerScope == null) {
      throw new IllegalStateException("This is top level scope. It doesn't have outer scope.");
    }
    return outerScope;
  }

  public String namesToString() {
    String outer = outerScope == null ? "" : outerScope.namesToString() + "\n";
    String inner = prettyPrint(bindings.keySet());
    return outer + inner;
  }

  @Override
  public String toString() {
    String outer = outerScope == null ? "" : outerScope.toString() + "\n";
    String inner = prettyPrint(bindings.entrySet());
    return outer + inner;
  }

  private String prettyPrint(Set<?> set) {
    return set.stream()
        .map(Object::toString)
        .map(s -> indent() + s)
        .collect(joining("\n"));
  }

  private String indent() {
    return outerScope == null ? "" : outerScope.indent() + "  ";
  }
}
