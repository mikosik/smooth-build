package org.smoothbuild.compile.fs.ps.ast.define;

import org.smoothbuild.compile.fs.lang.base.Nal;

/**
 * Referencable.
 */
public sealed interface ReferenceableP extends Nal
    permits ItemP, NamedEvaluableP {
  public String shortName();
}
