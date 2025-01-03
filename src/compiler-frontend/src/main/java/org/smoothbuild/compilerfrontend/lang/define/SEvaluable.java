package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.base.Identifiable;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Evaluable (function or value).
 */
public abstract sealed interface SEvaluable extends Identifiable
    permits SFunc, SNamedEvaluable, SValue {
  public SSchema schema();

  public String toSourceCode();
}
