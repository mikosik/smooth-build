package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.compilerfrontend.lang.type.STypeVar.typeParamsToSourceCode;

import org.smoothbuild.common.collect.Collection;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

/**
 * Named value.
 * This class is immutable.
 */
public sealed interface SNamedValue extends SValue, SNamedEvaluable
    permits SAnnotatedValue, SNamedExprValue {
  static String valueHeaderToSourceCode(SValue value, Collection<STypeVar> localTypeVars) {
    var schema = value.schema();
    return schema.type().specifier(localTypeVars) + " " + value.name()
        + typeParamsToSourceCode(schema.typeParams());
  }
}
