package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.base.IdentifiableCode;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Evaluable (function or value).
 */
public abstract sealed interface SEvaluable extends IdentifiableCode
    permits SFunc, SNamedEvaluable, SValue {
  public SSchema schema();

  public String toSourceCode();
}
