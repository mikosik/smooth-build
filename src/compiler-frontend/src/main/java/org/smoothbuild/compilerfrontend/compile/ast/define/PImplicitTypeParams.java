package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Check.checkInitializedToNotNull;

import org.jspecify.annotations.Nullable;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

public final class PImplicitTypeParams implements PTypeParams {
  private @Nullable List<STypeVar> typeVars;

  public void setTypeVars(List<STypeVar> typeVars) {
    this.typeVars = typeVars;
  }

  @Override
  public List<STypeVar> typeVars() {
    return checkInitializedToNotNull(typeVars, "typeVars");
  }
}
