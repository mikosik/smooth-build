package org.smoothbuild.lang.function.base;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public abstract class Scope<E> {
  public static <E> Scope<E> scope() {
    return scope(new EmptyScope<E>());
  }

  public static <E> Scope<E> scope(Scope<E> outerScope) {
    return new InnerScope<E>(outerScope);
  }

  public abstract boolean contains(Name name);

  public abstract E get(Name name);

  public abstract void add(Name name, E element);

  private static class InnerScope<E> extends Scope<E> {
    private final Scope<E> outerScope;
    private final Map<Name, E> bindings = new HashMap<>();

    public InnerScope(Scope<E> outerScope) {
      this.outerScope = outerScope;
    }

    public boolean contains(Name name) {
      return bindings.containsKey(name) || outerScope.contains(name);
    }

    public E get(Name name) {
      if (bindings.containsKey(name)) {
        return bindings.get(name);
      }
      return outerScope.get(name);
    }

    public void add(Name name, E element) {
      if (bindings.containsKey(name)) {
        throw new IllegalStateException("Name " + name + " is already bound in current scope.");
      }
      bindings.put(name, element);
    }
  }

  private static class EmptyScope<E> extends Scope<E> {
    public boolean contains(Name name) {
      return false;
    }

    public E get(Name name) {
      throw new NoSuchElementException(name.value());
    }

    public void add(Name name, E element) {
      throw new UnsupportedOperationException();
    }
  }

}
