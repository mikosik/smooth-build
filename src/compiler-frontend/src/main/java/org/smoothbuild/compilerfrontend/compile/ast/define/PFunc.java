package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;

public sealed interface PFunc extends PEvaluable permits PLambda, PNamedFunc {
  public PType resultType();

  public NList<PItem> params();

  @Override
  public SFuncType sType();

  public void setSType(SFuncType sFuncType);

  @Override
  public SFuncSchema schema();

  public void setSchema(SFuncSchema sFuncSchema);
}
