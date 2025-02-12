package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.type.SVarSet;

/**
 * Named value.
 * This class is immutable.
 */
public sealed interface SNamedValue extends SValue, SNamedEvaluable
    permits SAnnotatedValue, SNamedExprValue {
  static String valueHeaderToSourceCode(SValue value, SVarSet localVars) {
    var schema = value.schema();
    return schema.type().specifier(localVars) + " " + value.name()
        + schema.typeParams().toSourceCode();
  }
}
