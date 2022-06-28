package org.smoothbuild.parse.ast;

public sealed interface ExprP extends ObjP
    permits MonoExprP, RefP {
}
