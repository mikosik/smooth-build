package org.smoothbuild.lang.define;

import org.smoothbuild.lang.type.SchemaS;

public final class PolyValS extends PolyRefableS {
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
