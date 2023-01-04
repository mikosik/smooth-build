package org.smoothbuild.compile.fs.lang.define;

import org.smoothbuild.compile.fs.lang.base.Nal;
import org.smoothbuild.compile.fs.lang.type.SchemaS;

/**
 * Referencable.
 */
public sealed interface ReferenceableS
    extends Nal
    permits ItemS, NamedEvaluableS {
  public SchemaS schema();
}
