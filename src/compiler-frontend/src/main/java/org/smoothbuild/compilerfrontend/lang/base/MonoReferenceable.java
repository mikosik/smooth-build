package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Monomorphic referenceable.
 */
public interface MonoReferenceable extends Referenceable {
  public SType type();
}
