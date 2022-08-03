package org.smoothbuild.lang.define;

import org.smoothbuild.lang.type.SchemaS;

public final class PolyValS extends PolyRefableS {
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
