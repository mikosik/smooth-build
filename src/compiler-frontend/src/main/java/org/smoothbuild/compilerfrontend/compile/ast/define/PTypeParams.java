package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.type.STypeVarSet;

public sealed interface PTypeParams permits PExplicitTypeParams, PImplicitTypeParams {
  public STypeVarSet toTypeVarSet();
}
