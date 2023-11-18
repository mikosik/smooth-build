package org.smoothbuild.compile.frontend.lang.define;

import org.smoothbuild.compile.frontend.lang.base.Nal;
import org.smoothbuild.compile.frontend.lang.type.SchemaS;

/**
 * Referencable.
 */
public sealed interface ReferenceableS
    extends Nal
    permits ItemS, NamedEvaluableS {
  public SchemaS schema();
}
