package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.WithLoc;
import org.smoothbuild.lang.type.MonoTS;

public sealed abstract class MonoP extends WithLoc implements Parsed
    permits CallP, MonoNamedP, OrderP, SelectP {
  private Optional<? extends MonoTS> type;

  public MonoP(Loc loc) {
    super(loc);
  }

  @Override
  public Optional<? extends MonoTS> typeS() {
    return type;
  }

  public void setTypeS(MonoTS type) {
    setTypeS(Optional.of(type));
  }

  public void setTypeS(Optional<? extends MonoTS> type) {
    this.type = type;
  }
}
