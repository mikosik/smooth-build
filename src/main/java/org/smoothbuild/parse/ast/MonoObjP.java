package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.like.MonoObj;

public sealed interface MonoObjP extends MonoObj, ObjP
    permits CnstP, MonoExprP {
}
