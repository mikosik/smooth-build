package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.compilerfrontend.lang.type.STypeVarSet.sTypeVarSet;

import org.smoothbuild.compilerfrontend.lang.type.STypeVarSet;

public final class PImplicitTypeParams implements PTypeParams {
  @Override
  public STypeVarSet toTypeVarSet() {
    return sTypeVarSet();
  }
}
