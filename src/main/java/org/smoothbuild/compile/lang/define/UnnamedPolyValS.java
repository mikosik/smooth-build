package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.type.SchemaS;

public final class UnnamedPolyValS extends PolyEvaluableS  {
  public UnnamedPolyValS(ExprS exprS) {
    super(new SchemaS(exprS.evalT()), new UnnamedValS(exprS));
  }

  public UnnamedPolyValS(UnnamedValS unnamedValS) {
    super(new SchemaS(unnamedValS.type()), unnamedValS);
  }
}
