package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.type.STypeVarSet;

/**
 * Named value.
 * This class is immutable.
 */
public sealed interface SNamedValue extends SValue, SNamedEvaluable
    permits SAnnotatedValue, SNamedExprValue {
  static String valueHeaderToSourceCode(SValue value, STypeVarSet localTypeVars) {
    var schema = value.schema();
    return schema.type().specifier(localTypeVars) + " " + value.name()
        + schema.typeParams().toSourceCode();
  }
}
