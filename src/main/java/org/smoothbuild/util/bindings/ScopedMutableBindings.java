package org.smoothbuild.util.bindings;

public final class ScopedMutableBindings<E>
    extends ScopedBindings<E>
    implements MutableBindings<E> {
  ScopedMutableBindings(Bindings<? extends E> outerScopeBindings) {
    super(outerScopeBindings, new FlatMutableBindings<>());
  }

  @Override
  public E add(String name, E elem) {
    return ((MutableBindings<E>) innerScopeBindings()).add(name, elem);
  }
}
