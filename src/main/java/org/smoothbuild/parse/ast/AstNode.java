package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.define.Loc;
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

  public Optional<TypeS> type() {
    return type;
  }

  public void setType(TypeS type) {
    setType(Optional.of(type));
  }

  public void setType(Optional<TypeS> type) {
    this.type = type;
  }
}
