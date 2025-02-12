package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.type.SVarSet;

public sealed interface PTypeParams permits PExplicitTypeParams, PImplicitTypeParams {
  public SVarSet toVarSet();
}
