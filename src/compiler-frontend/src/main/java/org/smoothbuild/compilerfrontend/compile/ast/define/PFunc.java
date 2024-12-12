package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.base.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public sealed interface PFunc extends PEvaluable permits PLambda, PNamedFunc {
  public PType resultT();

  public NList<PItem> params();

  public default List<SType> paramTs() {
    return PItem.toSType(params());
  }

  @Override
  public SFuncType sType();

  public void setSType(SFuncType sFuncType);

  @Override
  public SFuncSchema sSchema();

  public void setSSchema(SFuncSchema sFuncSchema);
}
