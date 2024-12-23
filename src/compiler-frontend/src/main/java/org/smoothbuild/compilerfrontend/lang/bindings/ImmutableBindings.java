package org.smoothbuild.compilerfrontend.lang.bindings;

public sealed interface ImmutableBindings<E> extends Bindings<E>
    permits ImmutableFlatBindings, ImmutableScopedBindings {}
