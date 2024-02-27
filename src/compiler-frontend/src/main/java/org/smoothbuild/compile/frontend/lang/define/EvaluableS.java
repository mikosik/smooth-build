package org.smoothbuild.compile.frontend.lang.define;

import org.smoothbuild.compile.frontend.lang.base.location.Located;
import org.smoothbuild.compile.frontend.lang.type.SchemaS;

/**
 * Evaluable (function or value).
 */
public abstract sealed interface EvaluableS extends Located permits FuncS, NamedEvaluableS {
  public SchemaS schema();
}
