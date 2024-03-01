package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.base.Nal;

/**
 * Referencable.
 */
public sealed interface ReferenceableP extends Nal permits ItemP, NamedEvaluableP {
  public String shortName();
}
