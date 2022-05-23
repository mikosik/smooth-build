package org.smoothbuild.parse.ast;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.type.TypeS;

public final class ArgN extends NamedN {
  private final ExprN expr;

  public ArgN(String name, ExprN expr, Loc loc) {
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

  public String nameSanitized() {
    return declaresName() ? name() : "<nameless>";
  }

  public String typeAndName() {
    return type().map(TypeS::name).orElse("<missing type>") + ":" + nameSanitized();
  }

  public ExprN expr() {
    return expr;
  }
}
