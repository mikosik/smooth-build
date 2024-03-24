package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.base.location.Located;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

/**
 * Evaluable (function or value).
 */
public abstract sealed interface SEvaluable extends Located permits SFunc, SNamedEvaluable {
  public SchemaS schema();
}
