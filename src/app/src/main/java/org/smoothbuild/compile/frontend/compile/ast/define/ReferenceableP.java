package org.smoothbuild.compile.frontend.compile.ast.define;

import org.smoothbuild.compile.frontend.lang.base.Nal;

/**
 * Referencable.
 */
public sealed interface ReferenceableP extends Nal
    permits ItemP, NamedEvaluableP {
  public String shortName();
}
