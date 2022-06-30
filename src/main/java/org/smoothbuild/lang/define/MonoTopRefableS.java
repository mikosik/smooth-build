package org.smoothbuild.lang.define;

public sealed interface MonoTopRefableS extends TopRefableS, MonoObjS
    permits MonoFuncS, ValS {
}
