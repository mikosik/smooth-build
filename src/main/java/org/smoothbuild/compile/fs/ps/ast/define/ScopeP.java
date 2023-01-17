package org.smoothbuild.compile.fs.ps.ast.define;

import static org.smoothbuild.util.bindings.Bindings.immutableBindings;

import org.smoothbuild.util.bindings.ImmutableBindings;

public record ScopeP(
    String name,
    ImmutableBindings<ReferenceableP> referencables,
    ImmutableBindings<StructP> types) {
  public static ScopeP emptyScope() {
    return new ScopeP("", immutableBindings(), immutableBindings());
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
