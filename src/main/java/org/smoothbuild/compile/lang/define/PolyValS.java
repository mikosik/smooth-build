package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.type.SchemaS;

public final class PolyValS extends PolyEvaluableS {
  public PolyValS(SchemaS schema, ValS val) {
    super(schema, val);
  }

  @Override
  public ValS mono() {
    return ((ValS) super.mono());
  }
}
