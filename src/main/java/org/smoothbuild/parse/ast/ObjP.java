package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.like.Obj;

/**
 * Literal or expression in smooth language.
 */
public sealed interface ObjP extends Parsed, Obj
    permits ExprP, MonoObjP {
}
