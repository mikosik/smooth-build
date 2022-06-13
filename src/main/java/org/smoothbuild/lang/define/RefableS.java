package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Nal;
import org.smoothbuild.lang.like.Refable;

/**
 * Referencable.
 */
public sealed interface RefableS extends Refable, ObjS, Nal
    permits ItemS, TopRefableS {
}
