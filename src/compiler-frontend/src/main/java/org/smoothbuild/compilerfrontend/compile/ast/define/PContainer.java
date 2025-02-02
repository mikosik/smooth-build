package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.base.IdentifiableCode;

public sealed interface PContainer extends IdentifiableCode permits PEvaluable, PModule, PStruct {
  public void setScope(PScope scope);

  public PScope scope();
}
