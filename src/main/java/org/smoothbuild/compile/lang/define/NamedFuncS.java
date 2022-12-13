package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.Sanal;
import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.util.collect.NList;

/**
 * Named function.
 */
public sealed abstract class NamedFuncS
    extends Sanal
    implements FuncS, NamedEvaluableS
    permits AnnFuncS, NamedExprFuncS, ConstructorS {
  private final NList<ItemS> params;

  public NamedFuncS(FuncSchemaS schemaS, String name, NList<ItemS> params, Loc loc) {
    super(schemaS, name, loc);
    this.params = params;
  }

  @Override
  public FuncSchemaS schema() {
    return (FuncSchemaS) super.schema();
  }

  @Override
  public NList<ItemS> params() {
    return params;
  }
}
