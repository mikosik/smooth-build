package org.smoothbuild.compile.lang.define;

public sealed interface NamedPolyEvaluableS extends PolyEvaluableS, RefableS
    permits NamedPolyFuncS, NamedPolyValS {
}
