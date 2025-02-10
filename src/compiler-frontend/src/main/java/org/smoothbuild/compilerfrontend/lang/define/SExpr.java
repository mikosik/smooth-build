package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;

/**
 * Expression.
 */
public sealed interface SExpr extends HasLocation
    permits SCall, SCombine, SConstant, SInstantiate, SOrder, SSelect {
  public String toSourceCode(SVarSet localVars);

  public SType evaluationType();
}
