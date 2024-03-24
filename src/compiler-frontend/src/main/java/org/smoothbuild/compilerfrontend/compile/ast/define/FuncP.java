package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public sealed interface FuncP extends EvaluableP permits LambdaP, NamedFuncP {
  public TypeP resultT();

  public NList<ItemP> params();

  public default List<SType> paramTs() {
    return ItemP.toTypeS(params());
  }

  @Override
  public SFuncType typeS();

  public void setTypeS(SFuncType sFuncType);

  @Override
  public SFuncSchema schemaS();

  public void setSchemaS(SFuncSchema sFuncSchema);
}
