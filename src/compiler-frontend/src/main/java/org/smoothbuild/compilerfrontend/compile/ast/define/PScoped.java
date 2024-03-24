package org.smoothbuild.compilerfrontend.compile.ast.define;

public sealed interface PScoped permits PEvaluable, PModule, PStruct {
  public void setScope(PScope scope);

  public PScope scope();

  public String name();
}
