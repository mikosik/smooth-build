package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.base.Nal;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

/**
 * Referencable.
 */
public sealed interface ReferenceableS extends Nal permits ItemS, NamedEvaluableS {
  public SchemaS schema();
}
