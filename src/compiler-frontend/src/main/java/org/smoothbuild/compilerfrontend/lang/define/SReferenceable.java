package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.base.Nal;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Referencable.
 */
public sealed interface SReferenceable extends Nal permits SItem, SNamedEvaluable {
  public SSchema schema();
}
