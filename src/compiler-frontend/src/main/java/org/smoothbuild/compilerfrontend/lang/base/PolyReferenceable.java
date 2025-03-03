package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.compilerfrontend.lang.type.STypeScheme;

/**
 * Polymorphic referenceable.
 */
public interface PolyReferenceable extends Referenceable {
  public STypeScheme typeScheme();
}
