package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.WithLoc;
import org.smoothbuild.lang.type.TypeS;

public sealed class MonoAstNode extends WithLoc implements AstNode
    permits MonoExprN, MonoNamedN {
  private Optional<TypeS> type;

  public MonoAstNode(Loc loc) {
    super(loc);
  }

  @Override
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
