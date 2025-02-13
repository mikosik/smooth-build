package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypeVarSet;

/**
 * Expression.
 */
public sealed interface SExpr extends HasLocation
    permits SCall, SCombine, SConstant, SInstantiate, SOrder, SSelect {
  public String toSourceCode(STypeVarSet localTypeVars);

  public SType evaluationType();
}
