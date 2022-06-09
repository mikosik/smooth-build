package org.smoothbuild.lang.define;

public sealed interface MonoExprS extends MonoObjS
    permits CallS, OrderS, ParamRefS, SelectS, ObjRefS {
}
