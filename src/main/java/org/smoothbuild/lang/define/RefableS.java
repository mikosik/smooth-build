package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Nal;

/**
 * Referencable.
 */
public sealed interface RefableS extends ObjS, Nal
    permits ItemS, TopRefableS {
}
