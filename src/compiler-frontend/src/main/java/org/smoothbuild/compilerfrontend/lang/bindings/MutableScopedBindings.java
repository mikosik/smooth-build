package org.smoothbuild.compilerfrontend.lang.bindings;

public final class MutableScopedBindings<E> extends ScopedBindings<E>
    implements MutableBindings<E> {
  MutableScopedBindings(Bindings<? extends E> outerScopeBindings) {
    super(outerScopeBindings, new MutableFlatBindings<>());
  }

  @Override
  public E add(String name, E elem) {
    return innerScopeBindings().add(name, elem);
  }

  @Override
  public MutableBindings<E> innerScopeBindings() {
    // Cast is safe because constructor initializes innerScopeBindings with MutableBindings.
    @SuppressWarnings("unchecked")
    MutableBindings<E> mutableBindings = (MutableBindings<E>) super.innerScopeBindings();
    return mutableBindings;
  }
}
