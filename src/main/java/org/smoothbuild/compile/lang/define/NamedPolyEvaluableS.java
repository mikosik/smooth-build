package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.type.SchemaS;

public sealed abstract class NamedPolyEvaluableS extends PolyEvaluableS implements RefableS
    permits PolyFuncS, PolyValS {
  public NamedPolyEvaluableS(SchemaS schema, NamedEvaluableS mono) {
    super(schema, mono);
  }

  @Override
  public NamedEvaluableS mono() {
    return ((NamedEvaluableS) super.mono());
  }

  @Override
  public String name() {
    return mono().name();
  }
}
