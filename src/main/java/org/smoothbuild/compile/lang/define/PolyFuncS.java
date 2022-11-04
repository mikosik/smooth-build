package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;

import org.smoothbuild.compile.lang.type.FuncSchemaS;

public final class PolyFuncS extends PolyEvaluableS {
  public PolyFuncS(FuncSchemaS schema, FuncS funcS) {
    super(schema, funcS);
  }

  @Override
  public FuncS mono() {
    return (FuncS) super.mono();
  }

  @Override
  public String toString() {
    return "PolyFuncS(\n" + indent(polyEvaluableFieldsToString()) + "\n)";
  }
}
