package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.compilerfrontend.lang.type.STypeVar.typeParamsToSourceCode;

/**
 * Named value.
 * This class is immutable.
 */
public sealed interface SNamedValue extends SValue, SNamedEvaluable
    permits SAnnotatedValue, SNamedExprValue {
  static String valueHeaderToSourceCode(SValue value) {
    var schema = value.schema();
    return schema.type().specifier() + " " + value.name()
        + typeParamsToSourceCode(schema.typeParams());
  }
}
