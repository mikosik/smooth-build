package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.TypeS;

public sealed class AstNode permits ObjN, NamedN {
  private final Loc loc;
  private Optional<TypeS> type;

  public AstNode(Loc loc) {
    this.loc = loc;
  }

  public Loc loc() {
    return loc;
  }

  public Optional<TypeS> typeS() {
    return type;
  }

  public void setTypeS(TypeS type) {
    setTypeS(Optional.of(type));
  }

  public void setTypeS(Optional<TypeS> type) {
    this.type = type;
  }
}
