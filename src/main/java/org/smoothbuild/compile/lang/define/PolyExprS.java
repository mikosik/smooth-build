package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.SchemaS;

public sealed interface PolyExprS
    permits EvaluableRefS {
  public SchemaS schema();
  public Loc loc();
}
