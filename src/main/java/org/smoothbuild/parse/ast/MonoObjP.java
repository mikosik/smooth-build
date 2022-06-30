package org.smoothbuild.parse.ast;

public sealed interface MonoObjP extends ObjP
    permits CnstP, MonoExprP {
}
