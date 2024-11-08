package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

/**
 * Named value.
 * This class is immutable.
 */
public abstract sealed class SNamedValue extends SNamedEvaluable
    permits SAnnotatedValue, SNamedExprValue {
  public SNamedValue(SchemaS schema, String name, Location location) {
    super(schema, name, location);
  }

  protected String fieldsToString() {
    return list("schema = " + schema(), "name = " + name(), "location = " + location())
        .toString("\n");
  }
}
