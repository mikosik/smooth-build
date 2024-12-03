package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.Nal;
import org.smoothbuild.compilerfrontend.lang.base.Sanal;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Evaluable that has fully qualified name.
 */
public abstract sealed class SNamedEvaluable extends Sanal
    implements SEvaluable, SReferenceable, Nal permits SNamedFunc, SNamedValue {
  public SNamedEvaluable(SSchema schema, String name, Location location) {
    super(schema, name, location);
  }
}
