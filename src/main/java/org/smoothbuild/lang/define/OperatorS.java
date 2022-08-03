package org.smoothbuild.lang.define;

public sealed interface OperatorS extends ExprS
    permits CallS, MonoizeS, OrderS, ParamRefS, SelectS {
}
