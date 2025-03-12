package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;

public sealed interface PFunc extends PEvaluable permits PLambda, PNamedFunc {
  public PType resultType();

  public NList<PItem> params();

  @Override
  public default PType evaluationType() {
    return resultType();
  }

  @Override
  public default SFuncType type() {
    return new SFuncType(
        params().list().map(p -> p.pType().sType()), resultType().sType());
  }
}
