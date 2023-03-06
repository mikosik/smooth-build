package org.smoothbuild.util.bindings;

public final class ScopedImmutableBindings<E>
    extends ScopedBindings<E>
    implements ImmutableBindings<E> {
  ScopedImmutableBindings(
      ImmutableBindings<? extends E> outerScopeBindings,
      ImmutableBindings<? extends E> innerScopeBindings) {
    super(outerScopeBindings, innerScopeBindings);
  }
}
