package org.smoothbuild.compilerfrontend.lang.bindings;

public final class ImmutableScopedBindings<E> extends ScopedBindings<E>
    implements ImmutableBindings<E> {
  ImmutableScopedBindings(
      ImmutableBindings<? extends E> outerScopeBindings,
      ImmutableBindings<? extends E> innerScopeBindings) {
    super(outerScopeBindings, innerScopeBindings);
  }
}
