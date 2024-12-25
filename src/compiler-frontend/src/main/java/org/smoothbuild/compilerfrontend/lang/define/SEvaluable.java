package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Evaluable (function or value).
 */
public abstract sealed interface SEvaluable extends HasLocation
    permits SFunc, SNamedEvaluable, SValue {
  public SSchema schema();
}
