package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Nal;
import org.smoothbuild.lang.type.TypelikeS;

/**
 * Referencable.
 */
public sealed interface RefableS extends Nal
    permits ItemS, MonoRefableS, PolyRefableS {
  public ModPath modPath();
  public TypelikeS typelike();
}
