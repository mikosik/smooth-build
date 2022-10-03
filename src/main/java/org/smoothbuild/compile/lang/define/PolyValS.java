package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.type.SchemaS;

public final class PolyValS extends NamedPolyEvaluableS {
  private PolyValS(SchemaS schema, ValS val) {
    super(schema, val);
  }

  public static PolyValS polyValS(SchemaS schema, ValS value) {
    return new PolyValS(schema, value);
  }

  @Override
  public ValS mono() {
    return ((ValS) super.mono());
  }
}
