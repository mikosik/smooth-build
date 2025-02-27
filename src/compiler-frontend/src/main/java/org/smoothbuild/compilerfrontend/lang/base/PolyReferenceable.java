package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Polymorphic referenceable.
 */
public interface PolyReferenceable extends Referenceable {
  public SSchema schema();
}
