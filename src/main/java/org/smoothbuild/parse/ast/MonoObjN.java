package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.like.MonoObj;

public sealed interface MonoObjN extends AstNode, MonoObj, ObjN
    permits CnstN, MonoExprN {
}
