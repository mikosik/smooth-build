package org.smoothbuild.compile.frontend.compile.ast.define;

import static org.smoothbuild.common.bindings.Bindings.immutableBindings;

import org.smoothbuild.common.bindings.ImmutableBindings;

public record ScopeP(
    String name,
    ImmutableBindings<ReferenceableP> referencables,
    ImmutableBindings<StructP> types) {
  public static ScopeP emptyScope() {
    return new ScopeP("", immutableBindings(), immutableBindings());
  }

  @Override
  public String name() {
    return name;
  }

  public ScopeP newInnerScope(
      String name,
      ImmutableBindings<ReferenceableP> innerReferenceables,
      ImmutableBindings<StructP> innerTypes) {
    return new ScopeP(
        name,
        immutableBindings(referencables, innerReferenceables),
        immutableBindings(types, innerTypes));
  }
}
