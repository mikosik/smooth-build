package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.bindings.Bindings.immutableBindings;

import org.smoothbuild.common.bindings.ImmutableBindings;

public record SScope(
    ImmutableBindings<STypeDefinition> types, ImmutableBindings<SNamedEvaluable> evaluables) {

  public static SScope scopeS(SScope outer, SScope inner) {
    return new SScope(
        immutableBindings(outer.types(), inner.types()),
        immutableBindings(outer.evaluables(), inner.evaluables()));
  }
}
