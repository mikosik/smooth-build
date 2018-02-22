package org.smoothbuild.lang.function;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public abstract class Scope<E> {
  public static <E> Scope<E> scope() {
    return scope(new EmptyScope<E>());
  }

  public static <E> Scope<E> scope(Scope<E> outerScope) {
    return new InnerScope<>(outerScope);
  }

  public abstract boolean contains(String name);

  public abstract E get(String name);

  public abstract void add(String name, E element);

  public abstract Scope<E> outerScope();

  private static class InnerScope<E> extends Scope<E> {
    private final Scope<E> outerScope;
    private final Map<String, E> bindings = new HashMap<>();

    public InnerScope(Scope<E> outerScope) {
      this.outerScope = outerScope;
    }

    @Override
    public boolean contains(String name) {
      return bindings.containsKey(name) || outerScope.contains(name);
    }

    @Override
    public E get(String name) {
      if (bindings.containsKey(name)) {
        return bindings.get(name);
      }
      return outerScope.get(name);
    }

    @Override
    public void add(String name, E element) {
      if (bindings.containsKey(name)) {
        throw new IllegalStateException("Name " + name + " is already bound in current scope.");
      }
      bindings.put(name, element);
    }

    @Override
    public Scope<E> outerScope() {
      if (outerScope.getClass() == EmptyScope.class) {
        throw new UnsupportedOperationException();
      }
      return outerScope;
    }
  }

  private static class EmptyScope<E> extends Scope<E> {
    @Override
    public boolean contains(String name) {
      return false;
    }

    @Override
    public E get(String name) {
      throw new NoSuchElementException(name.toString());
    }

    @Override
    public void add(String name, E element) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Scope<E> outerScope() {
      throw new UnsupportedOperationException();
    }
  }
}
