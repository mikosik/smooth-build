package org.smoothbuild.parse.ast;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.like.Expr;
import org.smoothbuild.lang.type.TypeS;

public sealed abstract class ArgN extends NamedN permits DefaultArgN, ExplicitArgN {
  private final Expr expr;

  public ArgN(String name, Expr expr, Loc loc) {
    super(name, loc);
    this.expr = expr;
  }

  public boolean declaresName() {
    return super.name() != null;
  }

  @Override
  public String name() {
    checkState(declaresName());
    return super.name();
  }

  public abstract String nameSanitized();

  public String typeAndName() {
    return type().map(TypeS::name).orElse("<missing type>") + ":" + nameSanitized();
  }

  public Expr expr() {
    return expr;
  }
}
