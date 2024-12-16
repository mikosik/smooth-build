package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.base.HasIdAndLocation;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Referencable.
 */
public sealed interface SReferenceable extends HasIdAndLocation permits SItem, SNamedEvaluable {
  public SSchema schema();
}
