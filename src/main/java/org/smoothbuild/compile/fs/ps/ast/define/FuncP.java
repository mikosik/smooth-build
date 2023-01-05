package org.smoothbuild.compile.fs.ps.ast.define;

import org.smoothbuild.compile.fs.lang.type.FuncSchemaS;
import org.smoothbuild.compile.fs.lang.type.FuncTS;
import org.smoothbuild.compile.fs.lang.type.TypeS;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public sealed interface FuncP
    extends EvaluableP, ScopedP
    permits AnonymousFuncP, NamedFuncP {
  public TypeP resT();

  public NList<ItemP> params();

  public default ImmutableList<TypeS> paramTs() {
    return ItemP.toTypeS(params());
  }

  @Override
  public FuncTS typeS();

  public void setTypeS(FuncTS funcTS);

  @Override
  public FuncSchemaS schemaS();

  public void setSchemaS(FuncSchemaS funcSchemaS);
}
