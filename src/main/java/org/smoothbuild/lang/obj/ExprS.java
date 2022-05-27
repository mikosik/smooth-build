package org.smoothbuild.lang.obj;

public sealed interface ExprS extends ObjS
    permits CallS, OrderS, ParamRefS, SelectS, ObjRefS {
}
