package org.smoothbuild.parse.ast;

/**
 * Literal or expression in smooth language.
 */
public sealed interface ObjP extends Parsed
    permits ExprP, MonoObjP {
}
