package org.smoothbuild.util.bindings;

public sealed interface ImmutableBindings<E>
    extends Bindings<E>
    permits FlatImmutableBindings, ScopedImmutableBindings {
}
