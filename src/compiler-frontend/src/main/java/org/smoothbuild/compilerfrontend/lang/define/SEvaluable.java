package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.base.HasIdAndLocation;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Evaluable (function or value).
 */
public abstract sealed interface SEvaluable extends HasIdAndLocation
    permits SFunc, SNamedEvaluable, SValue {
  public SSchema schema();
}
