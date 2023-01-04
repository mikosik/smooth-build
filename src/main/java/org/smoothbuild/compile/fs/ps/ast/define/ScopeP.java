package org.smoothbuild.compile.fs.ps.ast.define;

import static org.smoothbuild.util.bindings.Bindings.immutableBindings;

import org.smoothbuild.util.bindings.ImmutableBindings;

public record ScopeP(
    ImmutableBindings<ReferenceableP> referencables,
    ImmutableBindings<StructP> types) {
  public ScopeP newInnerScope(
      ImmutableBindings<ReferenceableP> innerReferenceables,
      ImmutableBindings<StructP> innerTypes) {
    return new ScopeP(
        immutableBindings(referencables, innerReferenceables),
        immutableBindings(types, innerTypes));
  }
}
