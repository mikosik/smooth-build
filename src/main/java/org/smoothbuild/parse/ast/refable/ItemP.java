package org.smoothbuild.parse.ast.refable;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.NalImpl;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.parse.ast.AnnP;
import org.smoothbuild.parse.ast.expr.ExprP;
import org.smoothbuild.parse.ast.type.TypeP;

public final class ItemP extends NalImpl implements RefableP {
  private final TypeP type;
  private final Optional<ExprP> body;
  private TypeS typeS;

  public ItemP(TypeP type, String name, Optional<ExprP> body, Loc loc) {
    super(name, loc);
    this.type = type;
    this.body = body;
  }

  public TypeP type() {
    return type;
  }

  @Override
  public Optional<AnnP> ann() {
    return Optional.empty();
  }

  @Override
  public Optional<TypeP> evalT() {
    return Optional.of(type);
  }

  @Override
  public Optional<ExprP> body() {
    return body;
  }

  public TypeS typeS(){
    return typeS;
  }

  public void setTypeS(TypeS type) {
    this.typeS = type;
  }
}
