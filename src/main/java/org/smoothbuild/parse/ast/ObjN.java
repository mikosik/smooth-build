package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.like.Obj;

/**
 * Literal or expression in smooth language.
 */
public sealed interface ObjN extends AstNode, Obj
    permits ExprN, MonoObjN {
}
