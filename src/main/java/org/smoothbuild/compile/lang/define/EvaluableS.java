package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.WithLoc;
import org.smoothbuild.compile.lang.type.SchemaS;

/**
 * Evaluable (function or value).
 */
public abstract sealed interface EvaluableS extends WithLoc
    permits FuncS, NamedEvaluableS {
  public SchemaS schema();
}
