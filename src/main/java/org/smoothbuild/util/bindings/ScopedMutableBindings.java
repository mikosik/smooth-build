package org.smoothbuild.util.bindings;

public sealed class ScopedMutableBindings<E>
    extends ScopedBindings<E>
    implements MutableBindings<E>
    permits OptionalBindings {
  ScopedMutableBindings(Bindings<? extends E> outerScopeBindings) {
    super(outerScopeBindings, new FlatMutableBindings<>());
  }

  @Override
  public E add(String name, E elem) {
    return ((MutableBindings<E>) innerScopeBindings()).add(name, elem);
  }
}
