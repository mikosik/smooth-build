package org.smoothbuild.compile.fs.ps.ast.define;

public sealed interface ScopedP
    permits FuncP, ModuleP, NamedEvaluableP, StructP {
  public void setScope(ScopeP scope);

  public ScopeP scope();
}
