package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

/**
 * Polymorphic entity.
 */
public sealed interface SPolymorphic permits SLambda, SReference {
  public SchemaS schema();

  public Location location();
}
