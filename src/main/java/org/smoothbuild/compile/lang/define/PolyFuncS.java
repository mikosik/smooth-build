package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.type.SchemaS;

public sealed class PolyFuncS extends PolyEvaluableImplS implements PolyEvaluableS
    permits NamedPolyFuncS {
  public PolyFuncS(SchemaS schema, EvaluableS mono) {
    super(schema, mono);
  }
}
