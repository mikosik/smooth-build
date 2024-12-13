package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.Ial;
import org.smoothbuild.compilerfrontend.lang.base.Id;
import org.smoothbuild.compilerfrontend.lang.base.Saial;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Evaluable that has fully qualified name.
 */
public abstract sealed class SNamedEvaluable extends Saial
    implements SEvaluable, SReferenceable, Ial permits SNamedFunc, SNamedValue {
  public SNamedEvaluable(SSchema schema, Id id, Location location) {
    super(schema, id, location);
  }
}
