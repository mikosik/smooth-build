package org.smoothbuild.compile.ps.ast.expr;

import static org.smoothbuild.compile.ps.ast.expr.ItemP.toTypeS;

import java.util.Optional;

import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public sealed interface FuncP
    extends EvaluableP
    permits AnonymousFuncP, NamedFuncP {
  public Optional<TypeP> resT();

  public NList<ItemP> params();

  public default ImmutableList<TypeS> paramTs() {
    return toTypeS(params());
  }

  @Override
  public FuncTS typeS();

  public void setTypeS(FuncTS funcTS);

  @Override
  public FuncSchemaS schemaS();

  public void setSchemaS(FuncSchemaS funcSchemaS);
}
