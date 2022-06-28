package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.WithLoc;
import org.smoothbuild.lang.type.MonoTS;

public sealed abstract class MonoP extends WithLoc implements Parsed
    permits CallP, MonoNamedP, OrderP, SelectP {
  private Optional<MonoTS> type;

  public MonoP(Loc loc) {
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
