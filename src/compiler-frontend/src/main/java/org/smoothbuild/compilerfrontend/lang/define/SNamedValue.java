package org.smoothbuild.compilerfrontend.lang.define;

/**
 * Named value.
 * This class is immutable.
 */
public sealed interface SNamedValue extends SValue, SNamedEvaluable
    permits SAnnotatedValue, SNamedExprValue {
  static String valueHeaderToSourceCode(SValue value) {
    var schema = value.schema();
    return schema.type().toSourceCode() + " " + value.id().parts().getLast()
        + schema.quantifiedVars().toSourceCode();
  }
}
