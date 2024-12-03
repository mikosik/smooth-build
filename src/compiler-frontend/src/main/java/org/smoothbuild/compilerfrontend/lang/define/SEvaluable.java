package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.log.location.Located;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Evaluable (function or value).
 */
public abstract sealed interface SEvaluable extends Located permits SFunc, SNamedEvaluable {
  public SSchema schema();
}
