package org.smoothbuild.common.bindings;

public final class ScopedMutableBindings<E>
    extends ScopedBindings<E>
    implements MutableBindings<E> {
  ScopedMutableBindings(Bindings<? extends E> outerScopeBindings) {
    super(outerScopeBindings, new FlatMutableBindings<>());
  }

  @Override
  public E add(String name, E elem) {
    return innerScopeBindings().add(name, elem);
  }

  public MutableBindings<E> innerScopeBindings() {
    // Cast is safe because constructor initializes innerScopeBindings with MutableBindings.
    @SuppressWarnings("unchecked")
    MutableBindings<E> mutableBindings = (MutableBindings<E>) super.innerScopeBindings();
    return mutableBindings;
  }
}
