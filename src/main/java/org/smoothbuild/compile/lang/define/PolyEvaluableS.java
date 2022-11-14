package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.WithLoc;
import org.smoothbuild.compile.lang.type.SchemaS;

public sealed interface PolyEvaluableS extends WithLoc
    permits NamedPolyEvaluableS, PolyFuncS {
  public SchemaS schema();
  public EvaluableS mono();
}
