package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.WithLoc;
import org.smoothbuild.lang.type.TypeS;

public sealed abstract class GenericAstNode extends WithLoc implements AstNode
    permits ArgN, RefN {
  private Optional<? extends TypeS> type;

  public GenericAstNode(Loc loc) {
    super(loc);
  }

  @Override
  public Optional<? extends TypeS> typeO() {
    return type;
  }

  public void setTypeO(TypeS type) {
    setTypeO(Optional.of(type));
  }

  public void setTypeO(Optional<? extends TypeS> type) {
    this.type = type;
  }
}
