package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.base.IdentifiableCode;
import org.smoothbuild.compilerfrontend.lang.type.STypeScheme;

/**
 * Evaluable (function or value).
 */
public abstract sealed interface SEvaluable extends IdentifiableCode
    permits SFunc, SNamedEvaluable, SValue {
  public STypeScheme typeScheme();

  public String toSourceCode();
}
