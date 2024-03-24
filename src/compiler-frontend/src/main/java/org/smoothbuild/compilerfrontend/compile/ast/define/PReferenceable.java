package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.base.Nal;

/**
 * Referencable.
 */
public sealed interface PReferenceable extends Nal permits PItem, PNamedEvaluable {
  public String shortName();
}
