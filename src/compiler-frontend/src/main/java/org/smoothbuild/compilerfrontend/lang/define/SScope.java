package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.compilerfrontend.lang.bindings.Bindings.immutableBindings;

import org.smoothbuild.compilerfrontend.lang.bindings.ImmutableBindings;

public record SScope(
    ImmutableBindings<STypeDefinition> types, ImmutableBindings<SNamedEvaluable> evaluables) {

  public static SScope sScope(SScope outer, SScope inner) {
    return new SScope(
        immutableBindings(outer.types(), inner.types()),
        immutableBindings(outer.evaluables(), inner.evaluables()));
  }
}
