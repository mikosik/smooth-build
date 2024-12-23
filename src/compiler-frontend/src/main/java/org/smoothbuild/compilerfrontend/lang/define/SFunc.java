package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;

/**
 * This class and all its subclasses are immutable.
 */
public sealed interface SFunc extends SEvaluable permits SExprFunc, SNamedFunc {
  public NList<SItem> params();

  @Override
  public SFuncSchema schema();
}
