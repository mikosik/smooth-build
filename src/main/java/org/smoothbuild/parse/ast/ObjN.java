package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.like.Obj;
import org.smoothbuild.lang.type.TypeS;

/**
 * Literal or expression in smooth language.
 */
public sealed abstract class ObjN extends AstNode implements Obj
    permits CnstN, ExprN {
  public ObjN(Loc loc) {
    super(loc);
  }

  @Override
  public Optional<TypeS> typeO() {
    return type();
  }
}
