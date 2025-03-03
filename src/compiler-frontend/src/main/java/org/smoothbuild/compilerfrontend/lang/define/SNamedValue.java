package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

/**
 * Named value.
 * This class is immutable.
 */
public sealed interface SNamedValue extends SValue, SNamedEvaluable
    permits SAnnotatedValue, SNamedExprValue {
  static String valueHeaderToSourceCode(SValue value, Maybe<List<STypeVar>> typeParams) {
    return value.type().specifier() + " " + value.name()
        + typeParams.map(STypeVar::typeParamsToSourceCode).getOr("");
  }
}
