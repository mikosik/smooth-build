package org.smoothbuild.compile.fs.ps.ast.expr;

import static org.smoothbuild.util.bindings.Bindings.immutableBindings;

import org.smoothbuild.util.bindings.ImmutableBindings;

public record ScopeP(
    ImmutableBindings<RefableP> refables,
    ImmutableBindings<StructP> types) {
  public ScopeP newInnerScope(
      ImmutableBindings<RefableP> innerRefables,
      ImmutableBindings<StructP> innerTypes) {
    return new ScopeP(
        immutableBindings(refables, innerRefables),
        immutableBindings(types, innerTypes));
  }
}
