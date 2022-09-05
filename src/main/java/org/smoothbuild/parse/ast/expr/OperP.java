package org.smoothbuild.parse.ast.expr;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.WithLocImpl;
import org.smoothbuild.lang.type.TypeS;

public sealed abstract class OperP extends WithLocImpl implements ExprP
    permits CallP, DefaultArgP, NamedArgP, OrderP, SelectP {
  private TypeS type;

  public OperP(Loc loc) {
    super(loc);
  }

  public TypeS typeS(){
    return type;
  }

  public void setTypeS(TypeS type) {
    this.type = type;
  }
}
