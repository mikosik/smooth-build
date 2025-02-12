package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.smoothbuild.compilerfrontend.lang.type.SVarSet;

public final class PImplicitTypeParams implements PTypeParams {
  @Override
  public SVarSet toVarSet() {
    return varSetS();
  }
}
