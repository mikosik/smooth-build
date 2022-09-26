package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Nal;
import org.smoothbuild.compile.lang.type.TypelikeS;

/**
 * Referencable.
 */
public sealed interface RefableS extends Nal
    permits ItemS, PolyEvaluableS {
  public ModPath modPath();
  public TypelikeS typelike();
}
