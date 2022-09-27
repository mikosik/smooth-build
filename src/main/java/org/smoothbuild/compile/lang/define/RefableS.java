package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Nal;
import org.smoothbuild.compile.lang.type.TypelikeS;

/**
 * Referencable.
 */
public sealed interface RefableS extends Nal
    permits ItemS, NamedPolyEvaluableS {
  public ModPath modPath();
  public TypelikeS typelike();
}
