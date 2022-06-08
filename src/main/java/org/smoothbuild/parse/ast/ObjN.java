package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.like.Obj;

/**
 * Literal or expression in smooth language.
 */
public sealed abstract class ObjN extends AstNode implements Obj
    permits CnstN, ExprN {
  public ObjN(Loc loc) {
    super(loc);
  }
}
