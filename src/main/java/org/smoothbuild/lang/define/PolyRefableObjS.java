package org.smoothbuild.lang.define;

import org.smoothbuild.lang.like.PolyRefableObj;

public sealed interface PolyRefableObjS extends RefableObjS, PolyRefableObj, PolyObjS
    permits PolyFuncS {
}
