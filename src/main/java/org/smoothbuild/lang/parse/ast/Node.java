package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.type.impl.TypeS;

public sealed class Node permits ExprN, NamedN {
  private final Loc loc;
  private Optional<TypeS> type;

  public Node(Loc loc) {
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
