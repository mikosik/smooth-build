package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.base.IdentifiableCode;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

public sealed interface PContainer extends IdentifiableCode permits PEvaluable, PModule, PStruct {
  public void setScope(PScope scope);

  public void setFqn(Fqn fqn);

  public PScope scope();
}
