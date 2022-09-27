package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.type.SchemaS;
import org.smoothbuild.compile.lang.type.TypelikeS;

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
  public ModPath modPath() {
    return mono().modPath();
  }

  @Override
  public String name() {
    return mono().name();
  }

  @Override
  public TypelikeS typelike() {
    return schema();
  }
}
