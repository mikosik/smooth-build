package org.smoothbuild.compile.fs.lang.define;

import static org.smoothbuild.util.bindings.Bindings.immutableBindings;

import org.smoothbuild.util.bindings.ImmutableBindings;

public record ScopeS(
  ImmutableBindings<TypeDefinitionS> types,
  ImmutableBindings<NamedEvaluableS> evaluables) {

  public static ScopeS scopeS(ScopeS outer, ScopeS inner) {
    return new ScopeS(
        immutableBindings(outer.types(), inner.types()),
        immutableBindings(outer.evaluables(), inner.evaluables())
    );
  }
}
