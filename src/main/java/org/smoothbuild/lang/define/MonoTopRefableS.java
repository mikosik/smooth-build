package org.smoothbuild.lang.define;

import org.smoothbuild.lang.like.MonoTopRefable;

public sealed interface MonoTopRefableS extends TopRefableS, MonoObjS, MonoTopRefable
    permits MonoFuncS, ValS {
}
