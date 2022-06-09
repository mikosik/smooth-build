package org.smoothbuild.parse.ast;

public sealed interface MonoExprN extends ExprN, MonoObjN
    permits CallN, OrderN, SelectN {
}
