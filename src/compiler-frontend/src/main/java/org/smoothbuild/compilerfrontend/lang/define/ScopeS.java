package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.bindings.Bindings.immutableBindings;

import org.smoothbuild.common.bindings.ImmutableBindings;

public record ScopeS(
    ImmutableBindings<STypeDefinition> types, ImmutableBindings<SNamedEvaluable> evaluables) {

  public static ScopeS scopeS(ScopeS outer, ScopeS inner) {
    return new ScopeS(
        immutableBindings(outer.types(), inner.types()),
        immutableBindings(outer.evaluables(), inner.evaluables()));
  }
}
