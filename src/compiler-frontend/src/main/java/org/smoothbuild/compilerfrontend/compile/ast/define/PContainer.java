package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.base.Identifiable;

public sealed interface PContainer extends Identifiable permits PEvaluable, PModule, PStruct {
  public void setScope(PScope scope);

  public PScope scope();
}
