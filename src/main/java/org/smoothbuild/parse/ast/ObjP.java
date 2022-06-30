package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.like.common.ObjC;

/**
 * Literal or expression in smooth language.
 */
public sealed interface ObjP extends Parsed, ObjC
    permits ExprP, MonoObjP {
}
