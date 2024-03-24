package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

/**
 * Polymorphic entity.
 */
public sealed interface SPolymorphic permits SLambda, SReference {
  public SchemaS schema();

  public Location location();
}
