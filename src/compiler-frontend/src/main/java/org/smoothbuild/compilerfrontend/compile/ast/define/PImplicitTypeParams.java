package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

public final class PImplicitTypeParams implements PTypeParams {
  private List<STypeVar> typeVars;

  public void setTypeVars(List<STypeVar> typeVars) {
    this.typeVars = typeVars;
  }

  @Override
  public List<STypeVar> typeVars() {
    return typeVars;
  }
}
