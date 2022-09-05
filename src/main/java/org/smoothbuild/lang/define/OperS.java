package org.smoothbuild.lang.define;

/**
 * Operation.
 */
public sealed interface OperS extends ExprS
    permits CallS, MonoizeS, OrderS, ParamRefS, SelectS {
}
