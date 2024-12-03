package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Polymorphic entity.
 */
public sealed interface SPolymorphic permits SLambda, SReference {
  public SSchema schema();

  public Location location();
}
