package org.smoothbuild.lang.define;

import org.smoothbuild.lang.like.PolyTopRefable;

public sealed interface PolyTopRefableS extends TopRefableS, PolyTopRefable, PolyObjS
    permits PolyFuncS {
}
