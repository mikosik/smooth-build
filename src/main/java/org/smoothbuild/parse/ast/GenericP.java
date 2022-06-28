package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.WithLoc;
import org.smoothbuild.lang.type.TypeS;

public sealed abstract class GenericP extends WithLoc implements Parsed
    permits ArgP, RefP {
  private Optional<? extends TypeS> type;

  public GenericP(Loc loc) {
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
