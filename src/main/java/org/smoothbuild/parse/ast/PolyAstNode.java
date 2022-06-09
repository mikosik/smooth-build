package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.WithLoc;
import org.smoothbuild.lang.type.PolyTS;

public sealed abstract class PolyAstNode extends WithLoc implements AstNode
    permits PolyNamedN {
  private Optional<PolyTS> type;

  public PolyAstNode(Loc loc) {
    super(loc);
  }

  @Override
  public Optional<PolyTS> typeO() {
    return type;
  }

  public void setTypeO(PolyTS type) {
    setTypeO(Optional.of(type));
  }

  public void setTypeO(Optional<PolyTS> type) {
    this.type = type;
  }
}
