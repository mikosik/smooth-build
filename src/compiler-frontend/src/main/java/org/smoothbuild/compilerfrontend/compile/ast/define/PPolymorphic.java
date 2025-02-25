package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Polymorphic entity.
 */
public sealed interface PPolymorphic extends HasLocation permits PLambda, PReference {
  public SSchema schema();
}
