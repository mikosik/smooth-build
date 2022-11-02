package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.type.FuncSchemaS;

public final class PolyFuncS extends PolyEvaluableS {
  public PolyFuncS(FuncSchemaS schema, FuncS funcS) {
    super(schema, funcS);
  }

  @Override
  public FuncS mono() {
    return (FuncS) super.mono();
  }
}
