package org.smoothbuild.compile.ps.ast.expr;

public sealed interface WithScopeP
    permits FuncP, ModuleP, NamedEvaluableP, StructP {
  public void setScope(ScopeP scope);

  public ScopeP scope();
}
