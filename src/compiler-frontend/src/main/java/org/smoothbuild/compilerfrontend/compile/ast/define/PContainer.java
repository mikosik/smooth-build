package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.name.Fqn;

public sealed interface PContainer permits PEvaluable, PModule, PStruct {
  public void setScope(PScope scope);

  public Fqn fqn();

  public PScope scope();
}
