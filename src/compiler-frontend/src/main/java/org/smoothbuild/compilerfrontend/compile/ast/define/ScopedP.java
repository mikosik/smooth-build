package org.smoothbuild.compilerfrontend.compile.ast.define;

public sealed interface ScopedP permits EvaluableP, ModuleP, StructP {
  public void setScope(ScopeP scope);

  public ScopeP scope();

  public String name();
}
