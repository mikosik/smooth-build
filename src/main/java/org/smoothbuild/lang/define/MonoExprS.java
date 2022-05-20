package org.smoothbuild.lang.define;

public sealed interface MonoExprS extends MonoObjS
    permits CallS, MonoizeS, MonoRefS, OrderS, ParamRefS, SelectS {
}
