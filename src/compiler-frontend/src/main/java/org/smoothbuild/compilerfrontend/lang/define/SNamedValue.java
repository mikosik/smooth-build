package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.compilerfrontend.lang.type.STypeVar.typeParamsToSourceCode;

/**
 * Named value.
 * This class is immutable.
 */
public sealed interface SNamedValue extends SValue, SNamedEvaluable
    permits SAnnotatedValue, SNamedExprValue {
  static String valueHeaderToSourceCode(SValue value) {
    var scheme = value.typeScheme();
    return scheme.type().specifier() + " " + value.name()
        + typeParamsToSourceCode(scheme.typeParams());
  }
}
