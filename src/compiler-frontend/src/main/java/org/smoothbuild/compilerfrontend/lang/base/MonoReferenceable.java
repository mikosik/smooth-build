package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.compilerfrontend.lang.type.SSchema;

/**
 * Monomorphic referenceable.
 */
public interface MonoReferenceable extends Referenceable {
  public SSchema schema();
}
