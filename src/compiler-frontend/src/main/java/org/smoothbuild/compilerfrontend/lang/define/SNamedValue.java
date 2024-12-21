package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.collect.List.list;

/**
 * Named value.
 * This class is immutable.
 */
public sealed interface SNamedValue extends SValue, SNamedEvaluable
    permits SAnnotatedValue, SNamedExprValue {

  @Override
  public default String fieldsToString() {
    return list("schema = " + schema(), "name = " + id(), "location = " + location())
        .toString("\n");
  }
}
