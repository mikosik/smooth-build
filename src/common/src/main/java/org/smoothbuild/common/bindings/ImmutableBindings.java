package org.smoothbuild.common.bindings;

public sealed interface ImmutableBindings<E> extends Bindings<E>
    permits FlatImmutableBindings, ScopedImmutableBindings {}
