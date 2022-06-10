package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.WithLoc;
import org.smoothbuild.lang.type.MonoTS;

public sealed abstract class MonoAstNode extends WithLoc implements AstNode
    permits CallN, MonoNamedN, OrderN, SelectN {
  private Optional<MonoTS> type;

  public MonoAstNode(Loc loc) {
    super(loc);
  }

  @Override
  public Optional<MonoTS> typeO() {
    return type;
  }

  public void setTypeO(MonoTS type) {
    setTypeO(Optional.of(type));
  }

  public void setTypeO(Optional<MonoTS> type) {
    this.type = type;
  }
}
