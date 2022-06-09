package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.WithLoc;
import org.smoothbuild.lang.type.TypeS;

public sealed abstract class MonoAstNode extends WithLoc implements AstNode
    permits CallN, MonoNamedN, OrderN, SelectN {
  private Optional<TypeS> type;

  public MonoAstNode(Loc loc) {
    super(loc);
  }

  @Override
  public Optional<TypeS> typeO() {
    return type;
  }

  public void setTypeO(TypeS type) {
    setTypeO(Optional.of(type));
  }

  public void setTypeO(Optional<TypeS> type) {
    this.type = type;
  }
}
