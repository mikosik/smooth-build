package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.type.FuncSchemaS;

public final class PolyFuncS extends PolyRefableS {
  private PolyFuncS(FuncSchemaS schema, FuncS funcS) {
    super(schema, funcS);
  }

  public static PolyFuncS polyFuncS(FuncSchemaS schema, FuncS funcS) {
    return new PolyFuncS(schema, funcS);
  }

  @Override
  public FuncS mono() {
    return (FuncS) super.mono();
  }
}
