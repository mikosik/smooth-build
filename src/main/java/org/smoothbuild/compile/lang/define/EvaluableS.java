package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.location.Located;
import org.smoothbuild.compile.lang.type.SchemaS;

/**
 * Evaluable (function or value).
 */
public abstract sealed interface EvaluableS extends Located
    permits FuncS, NamedEvaluableS {
  public SchemaS schema();
}
