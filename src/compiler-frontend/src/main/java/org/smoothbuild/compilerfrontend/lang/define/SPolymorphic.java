package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Polymorphic entity.
 */
public sealed interface SPolymorphic extends HasLocation permits SLambda, SReference {
  public SSchema schema();
}
