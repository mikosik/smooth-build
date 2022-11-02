package org.smoothbuild.compile.lang.define;

/**
 * Operation.
 */
public sealed interface OperS extends ExprS
    permits CallS, OrderS, ParamRefS, PolyRefS, SelectS {
}
