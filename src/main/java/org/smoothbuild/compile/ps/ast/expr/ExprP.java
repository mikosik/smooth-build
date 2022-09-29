package org.smoothbuild.compile.ps.ast.expr;

import org.smoothbuild.compile.lang.base.WithLoc;
import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Expression in smooth language.
 */
public sealed interface ExprP extends WithLoc
    permits OperP, ValP {
  public TypeS typeS();
}
