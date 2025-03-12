package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.compilerfrontend.lang.base.Evaluable;
import org.smoothbuild.compilerfrontend.lang.base.IdentifiableCode;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

/**
 * Evaluable (function or value).
 */
public abstract sealed interface SEvaluable extends Evaluable, IdentifiableCode
    permits SFunc, SNamedEvaluable, SValue {
  @Override
  public SType type();

  public String toSourceCode();

  public String toSourceCode(Maybe<List<STypeVar>> typeParams);
}
