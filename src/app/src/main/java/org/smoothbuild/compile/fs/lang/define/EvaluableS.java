package org.smoothbuild.compile.fs.lang.define;

import org.smoothbuild.compile.fs.lang.base.location.Located;
import org.smoothbuild.compile.fs.lang.type.SchemaS;

/**
 * Evaluable (function or value).
 */
public abstract sealed interface EvaluableS extends Located
    permits FuncS, NamedEvaluableS {
  public SchemaS schema();
}
