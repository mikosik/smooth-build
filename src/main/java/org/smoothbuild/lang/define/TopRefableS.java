package org.smoothbuild.lang.define;

import org.smoothbuild.lang.like.TopRefable;

/**
 * Top level refable.
 */
public sealed interface TopRefableS extends TopRefable, RefableS
    permits FuncS, MonoTopRefableS, PolyTopRefableS {
}
