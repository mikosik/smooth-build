package org.smoothbuild.parse.ast.expr;

import org.smoothbuild.lang.base.WithLoc;

/**
 * Expression in smooth language.
 */
public sealed interface ExprP extends WithLoc
    permits OperP, ValP, RefP {
}
