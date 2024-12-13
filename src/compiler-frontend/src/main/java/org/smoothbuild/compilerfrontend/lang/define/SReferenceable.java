package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.base.Ial;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Referencable.
 */
public sealed interface SReferenceable extends Ial permits SItem, SNamedEvaluable {
  public SSchema schema();
}
