package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.base.Id;

public sealed interface PScoped permits PEvaluable, PModule, PStruct {
  public void setScope(PScope scope);

  public Id id();

  public PScope scope();
}
