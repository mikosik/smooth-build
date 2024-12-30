package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.name.Id;

public sealed interface PContainer permits PEvaluable, PModule, PStruct {
  public void setScope(PScope scope);

  public Id id();

  public PScope scope();
}
