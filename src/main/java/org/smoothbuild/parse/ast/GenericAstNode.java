package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.WithLoc;
import org.smoothbuild.lang.type.TKind;

public sealed abstract class GenericAstNode extends WithLoc implements AstNode
    permits ArgN, RefN {
  private Optional<? extends TKind> type;

  public GenericAstNode(Loc loc) {
    super(loc);
  }

  @Override
  public Optional<? extends TKind> typeO() {
    return type;
  }

  public void setTypeO(TKind type) {
    setTypeO(Optional.of(type));
  }

  public void setTypeO(Optional<? extends TKind> type) {
    this.type = type;
  }
}
