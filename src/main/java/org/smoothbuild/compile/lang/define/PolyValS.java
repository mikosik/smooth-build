package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.type.SchemaS;

public final class PolyValS extends NamedPolyEvaluableS {
  private PolyValS(SchemaS schema, NamedValS val) {
    super(schema, val);
  }

  public static PolyValS polyValS(SchemaS schema, NamedValS value) {
    return new PolyValS(schema, value);
  }

  @Override
  public NamedValS mono() {
    return ((NamedValS) super.mono());
  }
}
