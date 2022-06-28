package org.smoothbuild.parse.ast;

public sealed interface MonoExprP extends ExprP, MonoObjP
    permits CallP, OrderP, SelectP {
}
