package org.smoothbuild.compile.frontend.compile.ast.define;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compile.frontend.lang.type.FuncSchemaS;
import org.smoothbuild.compile.frontend.lang.type.FuncTS;
import org.smoothbuild.compile.frontend.lang.type.TypeS;

public sealed interface FuncP extends EvaluableP permits LambdaP, NamedFuncP {
  public TypeP resultT();

  public NList<ItemP> params();

  public default List<TypeS> paramTs() {
    return ItemP.toTypeS(params());
  }

  @Override
  public FuncTS typeS();

  public void setTypeS(FuncTS funcTS);

  @Override
  public FuncSchemaS schemaS();

  public void setSchemaS(FuncSchemaS funcSchemaS);
}
