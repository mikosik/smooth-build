package org.smoothbuild.compile.ps.ast.expr;

import org.smoothbuild.util.bindings.MutableBindings;

public record ScopeP(
    MutableBindings<RefableP> refables,
    MutableBindings<StructP> types) {
  public ScopeP newInnerScope() {
    return new ScopeP(new MutableBindings<>(refables), new MutableBindings<>(types));
  }
}
