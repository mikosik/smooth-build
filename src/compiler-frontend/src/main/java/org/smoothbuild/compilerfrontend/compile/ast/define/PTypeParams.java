package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

public sealed interface PTypeParams permits PExplicitTypeParams, PImplicitTypeParams {
  public List<STypeVar> explicitTypeVars();

  public List<STypeVar> typeVars();
}
