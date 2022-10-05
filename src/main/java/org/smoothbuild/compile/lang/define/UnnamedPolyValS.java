package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.type.SchemaS;

public final class UnnamedPolyValS extends PolyEvaluableS  {
  public UnnamedPolyValS(ExprS exprS) {
    super(new SchemaS(exprS.type()), new UnnamedValS(exprS));
  }
}