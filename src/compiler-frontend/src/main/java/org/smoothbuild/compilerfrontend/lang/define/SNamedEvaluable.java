package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.HasIdAndLocation;
import org.smoothbuild.compilerfrontend.lang.base.HasSchemaAndIdAndLocation;
import org.smoothbuild.compilerfrontend.lang.base.Id;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Evaluable that has fully qualified name.
 */
public abstract sealed class SNamedEvaluable extends HasSchemaAndIdAndLocation
    implements SEvaluable, SReferenceable, HasIdAndLocation permits SNamedFunc, SNamedValue {
  public SNamedEvaluable(SSchema schema, Id id, Location location) {
    super(schema, id, location);
  }
}
