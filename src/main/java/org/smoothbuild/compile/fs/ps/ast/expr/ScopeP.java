package org.smoothbuild.compile.fs.ps.ast.expr;

import static org.smoothbuild.util.bindings.Bindings.mutableBindings;

import org.smoothbuild.util.bindings.MutableBindings;

public record ScopeP(
    MutableBindings<RefableP> refables,
    MutableBindings<StructP> types) {
  public ScopeP newInnerScope() {
    return new ScopeP(mutableBindings(refables), mutableBindings(types));
  }
}
